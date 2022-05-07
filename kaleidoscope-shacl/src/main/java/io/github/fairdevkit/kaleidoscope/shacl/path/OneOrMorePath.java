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
package io.github.fairdevkit.kaleidoscope.shacl.path;

import java.util.Objects;
import java.util.Set;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

public class OneOrMorePath implements PropertyPath<PropertyPath<?>> {
    private PropertyPath<?> oneOrMore;

    @Override
    public PropertyPath<?> getPath() {
        return oneOrMore;
    }

    public void setPath(PropertyPath<?> path) {
        oneOrMore = path;
    }

    @Override
    public Set<? extends Value> resolve(Model model, Resource focusNode) {
        @SuppressWarnings("unchecked")
        var nodes = (Set<Value>)oneOrMore.resolve(model, focusNode);

        for (var node : nodes) {
            nodes.addAll(resolve(model, (Resource)node));
        }

        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OneOrMorePath p) {
            return Objects.equals(oneOrMore, p.oneOrMore);
        }
        return false;
    }
}
