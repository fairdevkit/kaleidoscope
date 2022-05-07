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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

public class Shape implements io.github.fairdevkit.kaleidoscope.model.Shape {
    private final Resource identifier;

    /* Targets */
    private final List<Value> nodeTargets;
    private final List<IRI> classBasedTargets;
    private final List<IRI> implicitClassTargets;
    private final List<IRI> subjectsOfTargets;
    private final List<IRI> objectsOfTargets;
    /* Declaring the Severity of a Shape */
    @Nullable private IRI severity;
    /* Declaring Messages for a Shape */
    private final List<Literal> message;
    /* Deactivating a Shape */
    private boolean deactivated;
    /* Value Type Constraint Components */
    final private List<IRI> _class;
    @Nullable private IRI datatype;
    @Nullable private IRI nodeKind;
    /* Value Range Constraint Components */
    @Nullable private Literal minExclusive;
    @Nullable private Literal minInclusive;
    @Nullable private Literal maxExclusive;
    @Nullable private Literal maxInclusive;
    /* String-based Constraint Components */
    @Nullable private Integer minLength;
    @Nullable private Integer maxLength;
    @Nullable private String pattern;
    @Nullable private String flags;
    private final List<String> languageIn;
    /* Property Pair Constraint Components */
    private final List<IRI> equals;
    private final List<IRI> disjoint;
    /* Logical Constraint Components */
    @Nullable private Shape not;
    private final List<Shape> and;
    private final List<Shape> or;
    private final List<Shape> xone;
    /* Shape-based Constraint Components */
    private final List<Shape> node;
    private final List<PropertyShape> property;
    /* Other Constraint Components */
    private boolean closed;
    private final List<IRI> ignoredProperties;
    @Nullable private Value hasValue;
    private final List<Value> in;

    public Shape(Resource identifier) {
        this.identifier = identifier;

        nodeTargets = new ArrayList<>();
        classBasedTargets = new ArrayList<>();
        implicitClassTargets = new ArrayList<>();
        subjectsOfTargets = new ArrayList<>();
        objectsOfTargets = new ArrayList<>();
        message = new ArrayList<>();
        _class = new ArrayList<>();
        languageIn = new ArrayList<>();
        equals = new ArrayList<>();
        disjoint = new ArrayList<>();
        and = new ArrayList<>();
        or = new ArrayList<>();
        xone = new ArrayList<>();
        node = new ArrayList<>();
        property = new ArrayList<>();
        ignoredProperties = new ArrayList<>();
        in = new ArrayList<>();
    }

    public Resource getIdentifier() {
        return identifier;
    }

    public List<Value> getNodeTargets() {
        return nodeTargets;
    }

    public void addNodeTarget(Value target) {
        nodeTargets.add(target);
    }

    public List<IRI> getClassBasedTargets() {
        return classBasedTargets;
    }

    public void addClassBasedTarget(IRI target) {
        classBasedTargets.add(target);
    }

    public List<IRI> getImplicitClassTargets() {
        return implicitClassTargets;
    }

    public void addImplicitClassTargets(IRI target) {
        implicitClassTargets.add(target);
    }

    public List<IRI> getSubjectsOfTargets() {
        return subjectsOfTargets;
    }

    public void addSubjectsOfTarget(IRI target) {
        subjectsOfTargets.add(target);
    }

    public List<IRI> getObjectsOfTargets() {
        return objectsOfTargets;
    }

    public void addObjectsOfTarget(IRI target) {
        objectsOfTargets.add(target);
    }

    public Optional<IRI> getSeverity() {
        return Optional.ofNullable(severity);
    }

    public void setSeverity(IRI severity) {
        this.severity = severity;
    }

    public List<Literal> getMessage() {
        return message;
    }

    public void addMessage(Literal message) {
        this.message.add(message);
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public List<IRI> get_Class() {
        return _class;
    }

    public void addClass(IRI _class) {
        this._class.add(_class);
    }

    public Optional<IRI> getDatatype() {
        return Optional.ofNullable(datatype);
    }

    public void setDatatype(IRI datatype) {
        this.datatype = datatype;
    }

    public Optional<IRI> getNodeKind() {
        return Optional.ofNullable(nodeKind);
    }

    public void setNodeKind(IRI nodeKind) {
        this.nodeKind = nodeKind;
    }

    public Optional<Literal> getMinExclusive() {
        return Optional.ofNullable(minExclusive);
    }

    public void setMinExclusive(Literal minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Optional<Literal> getMinInclusive() {
        return Optional.ofNullable(minInclusive);
    }

    public void setMinInclusive(Literal minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Optional<Literal> getMaxExclusive() {
        return Optional.ofNullable(maxExclusive);
    }

    public void setMaxExclusive(Literal maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public Optional<Literal> getMaxInclusive() {
        return Optional.ofNullable(maxInclusive);
    }

    public void setMaxInclusive(Literal maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Optional<Integer> getMinLength() {
        return Optional.ofNullable(minLength);
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public Optional<Integer> getMaxLength() {
        return Optional.ofNullable(maxLength);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public Optional<String> getPattern() {
        return Optional.ofNullable(pattern);
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Optional<String> getFlags() {
        return Optional.ofNullable(flags);
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public List<String> getLanguageIn() {
        return languageIn;
    }

    public void addLanguageIn(String lang) {
        languageIn.add(lang);
    }

    public List<IRI> getEquals() {
        return equals;
    }

    public void addEquals(IRI equals) {
        this.equals.add(equals);
    }

    public List<IRI> getDisjoint() {
        return disjoint;
    }

    public void addDisjoint(IRI disjoint) {
        this.disjoint.add(disjoint);
    }

    public Optional<Shape> getNot() {
        return Optional.ofNullable(not);
    }

    public void setNot(Shape not) {
        this.not = not;
    }

    public List<Shape> getAnd() {
        return and;
    }

    public void addAnd(Shape and) {
        this.and.add(and);
    }

    public List<Shape> getOr() {
        return or;
    }

    public void addOr(Shape or) {
        this.or.add(or);
    }

    public List<Shape> getXone() {
        return xone;
    }

    public void addXone(Shape xone) {
        this.xone.add(xone);
    }

    public List<Shape> getNode() {
        return node;
    }

    public void addNode(Shape node) {
        this.node.add(node);
    }

    public List<PropertyShape> getProperty() {
        return property;
    }

    public void addProperty(PropertyShape property) {
        this.property.add(property);
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public List<IRI> getIgnoredProperties() {
        return ignoredProperties;
    }

    public void addIgnoredProperties(IRI ignoredProperties) {
        this.ignoredProperties.add(ignoredProperties);
    }

    public Optional<Value> getHasValue() {
        return Optional.ofNullable(hasValue);
    }

    public void setHasValue(Value hasValue) {
        this.hasValue = hasValue;
    }

    public List<Value> getIn() {
        return in;
    }

    public void addIn(Value in) {
        this.in.add(in);
    }
}
