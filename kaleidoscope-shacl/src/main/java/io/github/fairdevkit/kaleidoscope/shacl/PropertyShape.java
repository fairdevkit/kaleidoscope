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
package io.github.fairdevkit.kaleidoscope.shacl;

import io.github.fairdevkit.kaleidoscope.shacl.path.PropertyPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

public class PropertyShape extends Shape implements io.github.fairdevkit.kaleidoscope.model.PropertyShape {
    /* SHACL Property Paths */
    private PropertyPath<?> path;
    /* Non-Validating Property Shape Characteristics */
    private final List<Literal> name;
    private final List<Literal> description;
    @Nullable private Integer order;
    @Nullable private Resource group;
    @Nullable private Value defaultValue;
    /* Cardinality Constraint Components */
    private Integer minCount;
    private Integer maxCount;
    /* String-based Constraint Components */
    @Nullable private Boolean uniqueLang;
    /* Property Pair Constraint Components */
    private final List<IRI> lessThan;
    private final List<IRI> lessThanOrEquals;
    /* Shape-based Constraint Components */
    @Nullable private Shape qualifiedValueShape;
    @Nullable private Boolean qualifiedValueShapesDisjoint;
    @Nullable private Integer qualifiedMinCount;
    @Nullable private Integer qualifiedMaxCount;

    public PropertyShape(Resource identifier) {
        super(identifier);

        name = new ArrayList<>();
        description = new ArrayList<>();
        lessThan = new ArrayList<>();
        lessThanOrEquals = new ArrayList<>();
    }

    public PropertyPath<?> getPath() {
        return path;
    }

    public void setPath(PropertyPath<?> path) {
        this.path = path;
    }

    public List<Literal> getName() {
        return name;
    }

    public void addName(Literal name) {
        this.name.add(name);
    }

    public List<Literal> getDescription() {
        return description;
    }

    public void addDescription(Literal description) {
        this.description.add(description);
    }

    public Optional<Integer> getOrder() {
        return Optional.ofNullable(order);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Optional<Resource> getGroup() {
        return Optional.ofNullable(group);
    }

    public void setGroup(Resource group) {
        this.group = group;
    }

    public Optional<Value> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public void setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Optional<Integer> getMinCount() {
        return Optional.ofNullable(minCount);
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public Optional<Integer> getMaxCount() {
        return Optional.ofNullable(maxCount);
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Optional<Boolean> isUniqueLang() {
        return Optional.ofNullable(uniqueLang);
    }

    public void setUniqueLang(boolean uniqueLang) {
        this.uniqueLang = uniqueLang;
    }

    public List<IRI> getLessThan() {
        return lessThan;
    }

    public void addLessThan(IRI lessThan) {
        this.lessThan.add(lessThan);
    }

    public List<IRI> getLessThanOrEquals() {
        return lessThanOrEquals;
    }

    public void addLessThanOrEquals(IRI lessThanOrEquals) {
        this.lessThanOrEquals.add(lessThanOrEquals);
    }

    public Optional<Shape> getQualifiedValueShape() {
        return Optional.ofNullable(qualifiedValueShape);
    }

    public void setQualifiedValueShape(Shape qualifiedValueShape) {
        this.qualifiedValueShape = qualifiedValueShape;
    }

    public Optional<Boolean> isQualifiedValueShapeDisjoint() {
        return Optional.ofNullable(qualifiedValueShapesDisjoint);
    }

    public void setQualifiedValueShapesDisjoint(boolean qualifiedValueShapesDisjoint) {
        this.qualifiedValueShapesDisjoint = qualifiedValueShapesDisjoint;
    }

    public Optional<Integer> getQualifiedMinCount() {
        return Optional.ofNullable(qualifiedMinCount);
    }

    public void setQualifiedMinCount(int qualifiedMinCount) {
        this.qualifiedMinCount = qualifiedMinCount;
    }

    public Optional<Integer> getQualifiedMaxCount() {
        return Optional.ofNullable(qualifiedMaxCount);
    }

    public void setQualifiedMaxCount(int qualifiedMaxCount) {
        this.qualifiedMaxCount = qualifiedMaxCount;
    }
}
