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

import io.github.fairdevkit.kaleidoscope.api.ShapeReader;
import io.github.fairdevkit.kaleidoscope.model.ShapeGraph;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.rdf4j.rio.RDFFormat;

public class ShaclShapeReader implements ShapeReader {
    @Override
    public ShapeGraph read(InputStream source) throws IOException{
        return read(source, RDFFormat.TURTLE);
    }

    public ShapeGraph read(InputStream source, RDFFormat format) throws IOException {
        return null;
    }

    @Override
    public NodeShape read(InputStream source, String subject) throws IOException {
        return read(source, subject, RDFFormat.TURTLE);
    }

    public NodeShape read(InputStream source, String subject, RDFFormat format) throws IOException {
        return null;
    }
}
