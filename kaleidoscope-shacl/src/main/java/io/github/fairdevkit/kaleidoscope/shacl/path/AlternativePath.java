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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

public class AlternativePath implements PropertyPath<List<PropertyPath<?>>> {
    private final List<PropertyPath<?>> alternatives;

    public AlternativePath() {
        alternatives = new ArrayList<>();
    }

    @Override
    public List<PropertyPath<?>> getPath() {
        return alternatives;
    }

    public void addPath(PropertyPath<?> path) {
        alternatives.add(path);
    }

    @Override
    public Set<? extends Value> resolve(Model model, Resource focusNode) {
        // in order, go through the paths, and the first to produce results will be chosen
        // and those value nodes will be returned

        for (var path : alternatives) {
            var valueNodes = path.resolve(model, focusNode);

            if (!valueNodes.isEmpty()) {
                return valueNodes;
            }
        }
        return Collections.emptySet();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AlternativePath p) {
            return Objects.equals(alternatives, p.alternatives);
        }
        return false;
    }
}
