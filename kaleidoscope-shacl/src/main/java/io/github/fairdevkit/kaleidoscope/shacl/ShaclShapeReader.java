/*
 * MIT License
 *
 * Copyright (c) 2021-2022 fairdevkit
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.fairdevkit.kaleidoscope.shacl;

import static io.github.fairdevkit.kaleidoscope.shacl.util.Utils.consumeBoolLiteral;
import static io.github.fairdevkit.kaleidoscope.shacl.util.Utils.consumeIntLiteral;
import static io.github.fairdevkit.kaleidoscope.shacl.util.Utils.consumeList;
import static io.github.fairdevkit.kaleidoscope.shacl.util.Utils.consumeLiteral;
import io.github.fairdevkit.kaleidoscope.api.ShapeReader;
import io.github.fairdevkit.kaleidoscope.model.ShapeGraph;
import io.github.fairdevkit.kaleidoscope.shacl.path.AlternativePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.InversePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.OneOrMorePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.PredicatePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.PropertyPath;
import io.github.fairdevkit.kaleidoscope.shacl.path.SequencePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.ZeroOrMorePath;
import io.github.fairdevkit.kaleidoscope.shacl.path.ZeroOrOnePath;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.ParseErrorLogger;

public class ShaclShapeReader implements ShapeReader<Shape> {
    @Override
    public ShapesGraph read(InputStream source) throws IOException {
        return readShapesGraph(source, null, RDFFormat.TURTLE);
    }

    @Override
    public ShapeGraph<Shape> read(InputStream source, @Nullable String base) throws IOException {
        return readShapesGraph(source, base, RDFFormat.TURTLE);
    }

    @Override
    public NodeShape readShape(InputStream source, String subject) throws IOException {
        return readShape(source, subject, null, RDFFormat.TURTLE);
    }

    @Override
    public NodeShape readShape(InputStream source, String subject, @Nullable String base) throws IOException {
        return readShape(source, subject, base, RDFFormat.TURTLE);
    }

    private ShapesGraph readShapesGraph(InputStream source, @Nullable String base, RDFFormat format) throws IOException {
        // TODO make this configurable; hardcoded for now to accomodate the SHACL testsuite
        var conf = new ParserConfig();
        conf.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);

        var model = Rio.parse(source, base, format, conf, SimpleValueFactory.getInstance(), new ParseErrorLogger());

        var subjects = new LinkedHashSet<Resource>();
        // determine node shapes
        // 1. get all explicit sh:NodeShape typed
        model.filter(null, RDF.TYPE, SHACL.NODE_SHAPE)
                .subjects()
                .forEach(subjects::add);
        // 2. get all implicit node shapes
        model.filter(null, SHACL.PROPERTY, null)
                .subjects()
                .forEach(subjects::add);

        subjects.removeIf(resource -> resource instanceof BNode);

        var graph = new ShapesGraph();
        model.getNamespaces().forEach(graph::addNamespace);

        subjects.stream()
                .map(subject -> readNodeShape(model, subject))
                .forEachOrdered(graph::addShape);

        model.filter(null, RDF.TYPE, SHACL.PROPERTY_SHAPE)
                .subjects()
                .stream()
                .filter(Predicate.not(subjects::contains))
                .map(subject -> readPropertyShape(model, subject))
                .forEachOrdered(graph::addShape);

        return graph;
    }

    private NodeShape readShape(InputStream source, String subject, String base, RDFFormat format) throws IOException {
        var model = Rio.parse(source, base, format);

        return readNodeShape(model, Values.iri(subject));
    }

    private Shape readShape(Model model, Resource subject) {
        // FIXME SHACL PropertyShape can also contain a sh:property
        if (model.contains(subject, SHACL.PROPERTY, null)) {
            return readNodeShape(model, subject);
        }
        if (model.contains(subject, SHACL.PATH, null)) {
            return readPropertyShape(model, subject);
        }

        return readShape(model, subject, new Shape(subject));
    }

    private <S extends Shape> S readShape(Model model, Resource subject, S shape) {
        // 2.1.3.1 Node targets (sh:targetNode)
        Models.getProperties(model, subject, SHACL.TARGET_NODE).forEach(shape::addNodeTarget);
        // 2.1.3.2 Class-based Targets (sh:targetClass)
        Models.getPropertyIRIs(model, subject, SHACL.TARGET_CLASS).forEach(shape::addClassBasedTarget);
        // 2.1.3.3 Implicit Class Targets
        if (model.contains(subject, RDF.TYPE, RDFS.CLASS)) {
            // TODO check for type of subject (fail on blank nodes?)
            shape.addImplicitClassTargets((IRI)subject);
        }
        // 2.1.3.4 Subjects-of targets (sh:targetSubjectsOf)
        Models.getPropertyIRIs(model, subject, SHACL.TARGET_SUBJECTS_OF).forEach(shape::addSubjectsOfTarget);
        // 2.1.3.5 Objects-of targets (sh:targetObjectsOf)
        Models.getPropertyIRIs(model, subject, SHACL.TARGET_OBJECTS_OF).forEach(shape::addObjectsOfTarget);

        // 2.1.4 Declaring the Severity of a Shape
        Models.getPropertyIRI(model, subject, SHACL.SEVERITY_PROP).ifPresent(shape::setSeverity);
        // 2.1.5 Declaring Messages for a Shape
        Models.getPropertyLiterals(model, subject, SHACL.MESSAGE).forEach(shape::addMessage);
        // 2.1.6 Deactivating a Shape
        consumeBoolLiteral(model, subject, SHACL.DEACTIVATED, shape::setDeactivated);

        // 4.1.1 sh:class
        Models.getPropertyIRIs(model, subject, SHACL.CLASS).forEach(shape::addClass);
        // 4.1.2 sh:datatype
        Models.getPropertyIRI(model, subject, SHACL.DATATYPE).ifPresent(shape::setDatatype);
        // 4.1.3 sh:nodeKind
        Models.getPropertyIRI(model, subject, SHACL.NODE_KIND_PROP).ifPresent(shape::setNodeKind);

        // 4.3.1 sh:minExclusive
        consumeLiteral(model, subject, SHACL.MIN_INCLUSIVE, Function.identity(), shape::setMinInclusive);
        // 4.3.2 sh:minInclusive
        consumeLiteral(model, subject, SHACL.MIN_EXCLUSIVE, Function.identity(), shape::setMinExclusive);
        // 4.3.3 sh:maxExclusive
        consumeLiteral(model, subject, SHACL.MAX_INCLUSIVE, Function.identity(), shape::setMaxInclusive);
        // 4.3.4 sh:maxInclusive
        consumeLiteral(model, subject, SHACL.MAX_EXCLUSIVE, Function.identity(), shape::setMaxExclusive);

        // 4.4.1 sh:minLength
        consumeIntLiteral(model, subject, SHACL.MIN_LENGTH, shape::setMinLength);
        // 4.4.2 sh:maxLength
        consumeIntLiteral(model, subject, SHACL.MAX_LENGTH, shape::setMaxLength);
        // 4.4.3 sh:pattern
        Models.getPropertyString(model, subject, SHACL.PATTERN).ifPresent(shape::setPattern);
        Models.getPropertyString(model, subject, SHACL.FLAGS).ifPresent(shape::setFlags);
        // 4.4.4 sh:languageIn
        Models.getPropertyResource(model, subject, SHACL.LANGUAGE_IN).ifPresent(list -> {
            consumeList(model, list, (Literal lang) -> shape.addLanguageIn(lang.stringValue()));
        });

        // 4.5.1 sh:equals
        Models.getPropertyIRIs(model, subject, SHACL.EQUALS).forEach(shape::addEquals);
        // 4.5.2 sh:disjoint
        Models.getPropertyIRIs(model, subject, SHACL.DISJOINT).forEach(shape::addDisjoint);

        // 4.6.1 sh:not
        Models.getPropertyResource(model, subject, SHACL.NOT)
                .map(not -> readShape(model, not))//TODO handle recursion
                .ifPresent(shape::setNot);
        // 4.6.2 sh:and
        Models.getPropertyResource(model, subject, SHACL.AND).ifPresent(list -> {
            consumeList(model, list, (Resource and) -> {
                var s = readShape(model, and);
                shape.addAnd(s);
            });
        });
        // 4.6.3 sh:or
        Models.getPropertyResource(model, subject, SHACL.OR).ifPresent(list -> {
            consumeList(model, list, (Resource or) -> {
                var s = readShape(model, or);
                shape.addOr(s);
            });
        });
        // 4.6.4 sh:xone
        Models.getPropertyResource(model, subject, SHACL.XONE).ifPresent(list -> {
            consumeList(model, list, (Resource xone) -> {
                var s = readShape(model, xone);
                shape.addXone(s);
            });
        });

        // 4.7.1 sh:node
        Models.getPropertyResources(model, subject, SHACL.NODE)
                .stream()
                .map(node -> readShape(model, node))
                .forEach(shape::addNode);
        // 4.7.2 sh:property
        Models.getPropertyResources(model, subject, SHACL.PROPERTY)
                .stream()
                .map(property -> readPropertyShape(model, property))
                .forEach(shape::addProperty);

        // 4.8.1 sh:closed, sh:ignoredProperties
        consumeBoolLiteral(model, subject, SHACL.CLOSED, shape::setClosed);
        Models.getPropertyResource(model, subject, SHACL.IGNORED_PROPERTIES).ifPresent(list -> {
            consumeList(model, list, shape::addIgnoredProperties);
        });
        // 4.8.2 sh:hasValue
        Models.getProperty(model, subject, SHACL.HAS_VALUE).ifPresent(shape::setHasValue);
        // 4.8.3 sh:in
        Models.getPropertyResource(model, subject, SHACL.IN).ifPresent(list -> {
            consumeList(model, list, shape::addIn);
        });

        return shape;
    }

    private NodeShape readNodeShape(Model model, Resource subject) {
        return readShape(model, subject, new NodeShape(subject));
    }

    private PropertyShape readPropertyShape(Model model, Resource subject) {
        var shape = readShape(model, subject, new PropertyShape(subject));

        // 2.3.1 SHACL Property Paths
        Models.getProperty(model, subject, SHACL.PATH).ifPresentOrElse(path -> {
            consumePropertyPath(model, path, shape::setPath, false);
        }, () -> {
            throw new ShaclShapeReaderException("Invalid property shape; no sh:path present");
        });

        // 2.3.2.1 sh:name and sh:description
        Models.getPropertyLiterals(model, subject, SHACL.NAME).forEach(shape::addName);
        Models.getPropertyLiterals(model, subject, SHACL.DESCRIPTION).forEach(shape::addDescription);
        // 2.3.2.2 sh:order
        consumeIntLiteral(model, subject, SHACL.ORDER, shape::setOrder);
        // 2.3.2.3 sh:group
        Models.getPropertyIRI(model, subject, SHACL.GROUP).ifPresent(shape::setGroup);
        // 2.3.2.4 sh:defaultValue
        Models.getProperty(model, subject, SHACL.DEFAULT_VALUE).ifPresent(shape::setDefaultValue);

        // 4.2.1 sh:minCount
        consumeIntLiteral(model, subject, SHACL.MIN_COUNT, shape::setMinCount);
        // 4.2.2 sh:maxCount
        consumeIntLiteral(model, subject, SHACL.MAX_COUNT, shape::setMaxCount);

        // 4.4.5 sh:uniqueLang
        consumeBoolLiteral(model, subject, SHACL.UNIQUE_LANG, shape::setUniqueLang);

        // 4.5.3 sh:lessThan
        Models.getPropertyIRIs(model, subject, SHACL.LESS_THAN).forEach(shape::addLessThan);
        // 4.5.4 sh:lessThanOrEquals
        Models.getPropertyIRIs(model, subject, SHACL.LESS_THAN_OR_EQUALS).forEach(shape::addLessThanOrEquals);

        // 4.7.3 sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount
        Models.getPropertyResource(model, subject, SHACL.QUALIFIED_VALUE_SHAPE)
                .map(s -> readShape(model, s))
                .ifPresent(shape::setQualifiedValueShape);
        consumeBoolLiteral(model, subject, SHACL.QUALIFIED_VALUE_SHAPES_DISJOINT, shape::setQualifiedValueShapesDisjoint);
        consumeIntLiteral(model, subject, SHACL.QUALIFIED_MIN_COUNT, shape::setQualifiedMinCount);
        consumeIntLiteral(model, subject, SHACL.QUALIFIED_MAX_COUNT, shape::setQualifiedMaxCount);

        return shape;
    }

    private void consumePropertyPath(Model model, Value propertyPath, Consumer<PropertyPath<?>> consumer, boolean inverse) {
        if (propertyPath instanceof IRI iri) {
            // 2.3.1.1 Predicate Paths
            consumer.accept(new PredicatePath(iri, inverse));
        } else if (propertyPath instanceof BNode subject) {
            // 2.3.1.2 Sequence Paths
            Models.getPropertyResource(model, subject, RDF.FIRST).ifPresent(x -> {
                var sequence = new SequencePath();
                consumeList(model, subject, path -> consumePropertyPath(model, path, sequence::addPath, inverse));

                if (sequence.getPath().size() < 2) {
                    throw new RuntimeException("sequence path should contain at least 2 entries");
                }

                consumer.accept(sequence);
            });

            // 2.3.1.3 Alternative Paths
            Models.getPropertyResource(model, subject, SHACL.ALTERNATIVE_PATH).ifPresent(list -> {
                var alternative = new AlternativePath();
                consumeList(model, list, (Resource path) -> consumePropertyPath(model, path, alternative::addPath, inverse));

                if (alternative.getPath().size() < 2) {
                    throw new RuntimeException("sh:alternativePath should contain at least 2 entries");
                }

                consumer.accept(alternative);
            });

            // 2.3.1.4 Inverse Paths
            Models.getPropertyResource(model, subject, SHACL.INVERSE_PATH).ifPresent(path -> {
                var inversePath = new InversePath();
                consumePropertyPath(model, path, inversePath::setPath, true);
                consumer.accept(inversePath);
            });

            // 2.3.1.5 Zero-Or-More Paths
            Models.getPropertyResource(model, subject, SHACL.ZERO_OR_MORE_PATH).ifPresent(path -> {
                var zeroOrMore = new ZeroOrMorePath();
                consumePropertyPath(model, path, zeroOrMore::setPath, inverse);
                consumer.accept(zeroOrMore);
            });

            // 2.3.1.6 One-Or-More Paths
            Models.getPropertyResource(model, subject, SHACL.ONE_OR_MORE_PATH).ifPresent(path -> {
                var oneOrMore = new OneOrMorePath();
                consumePropertyPath(model, path, oneOrMore::setPath, inverse);
                consumer.accept(oneOrMore);
            });

            // 2.3.1.7 Zero-Or-One Paths
            Models.getPropertyResource(model, subject, SHACL.ZERO_OR_ONE_PATH).ifPresent(path -> {
                var zeroOrOne = new ZeroOrOnePath();
                consumePropertyPath(model, path, zeroOrOne::setPath, inverse);
                consumer.accept(zeroOrOne);
            });
        }
    }
}
