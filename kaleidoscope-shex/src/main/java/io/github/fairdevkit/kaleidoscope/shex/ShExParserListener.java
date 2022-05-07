/*
 * MIT License
 *
 * Copyright (c) 2021 fairdevkit
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
package io.github.fairdevkit.kaleidoscope.shex;

import io.shex.ShExDocBaseListener;
import io.shex.ShExDocParser;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.concurrent.NotThreadSafe;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.util.Values;

@NotThreadSafe
public class ShExParserListener extends ShExDocBaseListener {
    private final Schema schema;
    private final Map<String, String> namespaces;

    public ShExParserListener(Schema schema) {
        this.schema = schema;
        namespaces = new HashMap<>();
    }

    @Override
    public void enterBaseDecl(ShExDocParser.BaseDeclContext ctx) {
        // TODO
    }

    @Override
    public void enterImportDecl(ShExDocParser.ImportDeclContext ctx) {
        var iri = stripAngularBrackets(ctx.IRIREF());
        schema.addImport(Values.iri(iri));
    }

    @Override
    public void enterPrefixDecl(ShExDocParser.PrefixDeclContext ctx) {
        var prefix = ctx.PNAME_NS().getText();
        prefix = prefix.substring(0, prefix.length() - 1);

        var iri = stripAngularBrackets(ctx.IRIREF());

        schema.addNamespace(Values.namespace(prefix, iri));
        namespaces.put(prefix, iri);
    }

    @Override
    public void enterShapeExprDecl(ShExDocParser.ShapeExprDeclContext ctx) {
        // start new shapeExpression on stack
        System.out.println("enter shape expr decl");
    }

    @Override
    public void exitShapeExprDecl(ShExDocParser.ShapeExprDeclContext ctx) {
        // finish shape expr on stack
        System.out.println("exit shape expr decl");
    }

    @Override
    public void enterShapeExpression(ShExDocParser.ShapeExpressionContext ctx) {
        System.out.println("enter shape expression");
        System.out.println(ctx.getText());

        Optional.ofNullable(ctx.shapeOr()).ifPresent(or -> {
            System.out.println("or");
            for (var and : or.shapeAnd()) {
                System.out.println("and");
                for (var not : and.shapeNot()) {
                    System.out.println("not, atom");
                    var atom = not.shapeAtom();
                    System.out.println("atom type: " + atom.getClass());
                    System.out.println(atom.getText());

//                    if (atom instanceof ShExDocParser.ShapeAtomNonLitNodeConstraintContext x) {
//                        x.nonLitNodeConstraint().
//                    }
                }
            }
        });
    }

    @Override
    public void enterLitNodeConstraintStringFacet(ShExDocParser.LitNodeConstraintStringFacetContext ctx) {
        System.out.println("enter lit node constraint string facet");
        System.out.println(">> "+ctx.getText());
    }
    @Override
    public void enterInlineShapeAtomNonLitNodeConstraint(ShExDocParser.InlineShapeAtomNonLitNodeConstraintContext ctx) {
        System.out.println("enter inline shape atom non lit node constraint");
        System.out.println(">> "+ctx.getText());
    }
    @Override
    public void enterShapeAtomLitNodeConstraint(ShExDocParser.ShapeAtomLitNodeConstraintContext ctx) {
        System.out.println("enter shape atom lit node constraint");
        System.out.println(">> "+ctx.getText());
    }
    @Override
    public void enterNonLitNodeConstraint(ShExDocParser.NonLitNodeConstraintContext ctx) {
        System.out.println("enter non lit node constraint");
        System.out.println(">> "+ctx.getText());
        System.out.println(">> "+ctx.inlineNonLitNodeConstraint().getClass());
    }

    @Override
    public void enterLitNodeConstraintLiteral(ShExDocParser.LitNodeConstraintLiteralContext ctx) {
        System.out.println("enter lit node constraint literal");
        System.out.println(">> "+ctx.getText());
        System.out.println(">> "+ctx.nonLiteralKind());
        System.out.println(">> "+ctx.stringFacet());
    }

    @Override
    public void enterShapeDefinition(ShExDocParser.ShapeDefinitionContext ctx) {
        System.out.println("enter shape definition");
        System.out.println(ctx.getText());
    }

    @Override
    public void enterQualifier(ShExDocParser.QualifierContext ctx) {
        Optional.ofNullable(ctx.KW_CLOSED())
                .ifPresent(kw -> {/*TODO set closed flag*/});

        Optional.ofNullable(ctx.extraPropertySet())
                .map(ShExDocParser.ExtraPropertySetContext::predicate)
                .stream()
                .flatMap(Collection::stream)
                .map(ShExDocParser.PredicateContext::iri)
                .map(this::getIri)
                .forEach(iri -> {/*TODO set extra properties*/});
    }

    @Override
    public void enterShapeAnd(ShExDocParser.ShapeAndContext ctx) {
        // TODO
    }

    @Override
    public void enterTripleExpression(ShExDocParser.TripleExpressionContext ctx) {

    }

    @Override
    public void enterTripleConstraint(ShExDocParser.TripleConstraintContext ctx) {
        // TODO

        Optional.ofNullable(ctx.predicate().iri())
                .map(this::getIri)
                .ifPresent(iri -> {/*TODO set iri*/});

        if (ctx.cardinality() == null) {
            // TODO setMinCount 1
            // TODO setMaxCount 1
        }
    }

    @Override
    public void exitTripleConstraint(ShExDocParser.TripleConstraintContext ctx) {
        // TODO
    }

    @Override
    public void enterIriRange(ShExDocParser.IriRangeContext ctx) {
        var iri = getIri(ctx.iri());
        // TODO set value range IRI
    }

    @Override
    public void enterNodeConstraintDatatype(ShExDocParser.NodeConstraintDatatypeContext ctx) {
        Optional.ofNullable(ctx.datatype())
                .map(ShExDocParser.DatatypeContext::iri)
                .map(this::getIri)
                .ifPresent(datatype -> {/*TODO set shape datatype*/});
    }

    @Override
    public void enterNodeConstraintLiteral(ShExDocParser.NodeConstraintLiteralContext ctx) {
        Optional.ofNullable(ctx.KW_LITERAL())
                .ifPresent(kw -> {/*TODO set literal nodekind*/});
    }

    @Override
    public void enterNonLiteralKind(ShExDocParser.NonLiteralKindContext ctx) {
        Optional.ofNullable(ctx.KW_IRI())
                .ifPresent(kw -> {/*TODO set iri nodekind*/});

        Optional.ofNullable(ctx.KW_BNODE())
                .ifPresent(kw -> {/*TODO set bnode nodekind*/});

        Optional.ofNullable(ctx.KW_NONLITERAL())
                .ifPresent(kw -> {/*TODO set iri-or-bnode nodekind*/});
    }

    /* Cardinality constraints */

    @Override
    public void enterStarCardinality(ShExDocParser.StarCardinalityContext ctx) {
        //TODO set mincount 0
    }

    @Override
    public void enterPlusCardinality(ShExDocParser.PlusCardinalityContext ctx) {
        //TODO set mincount 1
    }

    @Override
    public void enterOptionalCardinality(ShExDocParser.OptionalCardinalityContext ctx) {
        //TODO set mincount 0, maxcount 1
    }

    @Override
    public void enterExactRange(ShExDocParser.ExactRangeContext ctx) {
        var text = ctx.INTEGER().getText();
        var exact = Integer.parseInt(text);
        //TODO set mincount exact, maxcount exact
    }

    @Override
    public void enterMinMaxRange(ShExDocParser.MinMaxRangeContext ctx) {
        var minText = ctx.INTEGER(0).getText();
        var min = Integer.parseInt(minText);
        // TODO set mincount

        if (ctx.INTEGER().size() == 2) {
            var maxText = ctx.INTEGER(1).getText();
            var max = Integer.parseInt(maxText);
            // TODO set maxcount
        }
    }

    @Override
    public void enterNumericFacet(ShExDocParser.NumericFacetContext ctx) {
        Optional.ofNullable(ctx.numericRange()).ifPresent(range -> {
            var raw = ctx.rawNumeric();

            var number = Optional.ofNullable(raw.INTEGER())
                    .map(TerminalNode::getText)
                    .<Number>map(Integer::parseInt)
                    .or(() -> Optional.ofNullable(raw.DOUBLE())
                            .map(TerminalNode::getText)
                            .map(Double::parseDouble))
                    .or(() -> Optional.ofNullable(raw.DECIMAL())
                            .map(TerminalNode::getText)
                            .map(BigDecimal::new))
                    .orElseThrow();

            Optional.ofNullable(range.KW_MINEXCLUSIVE())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(range.KW_MININCLUSIVE())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(range.KW_MAXEXCLUSIVE())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(range.KW_MAXINCLUSIVE())
                    .ifPresent(kw -> {/*TODO set number*/});
        });

        Optional.ofNullable(ctx.numericLength()).ifPresent(length -> {
            var number = Integer.parseInt(ctx.INTEGER().getText());

            Optional.ofNullable(length.KW_TOTALDIGITS())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(length.KW_FRACTIONDIGITS())
                    .ifPresent(kw -> {/*TODO set number*/});
        });
    }

    @Override
    public void enterStringFacet(ShExDocParser.StringFacetContext ctx) {
        Optional.ofNullable(ctx.stringLength()).ifPresent(length -> {
            var number = Integer.parseInt(ctx.INTEGER().getText());

            Optional.ofNullable(length.KW_LENGTH())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(length.KW_MINLENGTH())
                    .ifPresent(kw -> {/*TODO set number*/});
            Optional.ofNullable(length.KW_MAXLENGTH())
                    .ifPresent(kw -> {/*TODO set number*/});
        });

        Optional.ofNullable(ctx.REGEXP())
                .map(TerminalNode::getText)
                .map(regexp -> {
                    // TODO determine how to handle regex: compile into pattern, or store as raw string
                    return null;
                });
    }

    /* Utility methods */

    private String stripAngularBrackets(TerminalNode iriRef) {
        var iri = iriRef.getText();
        return iri.substring(1, iri.length() - 1);
    }

    private IRI getIri(ShExDocParser.IriContext ctx) {
        var resolvedPrefixedName = Optional.ofNullable(ctx.prefixedName())
                .map(pname -> Optional.ofNullable(pname.PNAME_LN()).orElseGet(pname::PNAME_NS))
                .map(this::resolvePrefixedName);

        var iri = Optional.ofNullable(ctx.IRIREF())
                .map(this::stripAngularBrackets);

        return resolvedPrefixedName.or(() -> iri)
                .map(Values::iri)
                .orElseThrow();
    }

    private String resolvePrefixedName(TerminalNode ctx) {
        var prefixedName = ctx.getText();

        var colonIndex = prefixedName.indexOf(':');
        var startOffset = prefixedName.charAt(0) == '@' ? 1 : 0;
        var prefix = prefixedName.substring(startOffset, colonIndex);

        if (!namespaces.containsKey(prefix)) {
            throw new RuntimeException("Encountered undeclared namespace prefix '"+prefix+"' at line '"+ctx.getSymbol().getLine()+"', character '"+ctx.getSymbol().getCharPositionInLine()+"'");
        }

        var namespace = namespaces.get(prefix);
        var localName = prefixedName.substring(colonIndex + 1);

        return namespace.concat(localName);
    }
}
