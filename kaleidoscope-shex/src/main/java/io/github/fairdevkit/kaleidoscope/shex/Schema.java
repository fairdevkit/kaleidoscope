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

import io.github.fairdevkit.kaleidoscope.model.ShapeGraph;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;

public class Schema implements ShapeGraph<ShapeExpression> {
    private final Collection<Namespace> namespaces;
    private final Collection<IRI> imports;
    private final Collection<ShapeExpression> shapes;

    public Schema() {
        namespaces = new ArrayList<>();
        imports = new ArrayList<>();
        shapes = new ArrayList<>();
    }

    public Collection<Namespace> getNamespaces() {
        return namespaces;
    }

    public void addNamespace(Namespace namespace) {
        namespaces.add(namespace);
    }

    public Collection<IRI> getImports() {
        return imports;
    }

    public void addImport(IRI importIri) {
        imports.add(importIri);
    }

    @Override
    public Collection<ShapeExpression> getShapes() {
        return shapes;
    }

    public void addShape(ShapeExpression shape) {
        shapes.add(shape);
    }
}
