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
package io.github.fairdevkit.kaleidoscope.shex;

import io.github.fairdevkit.kaleidoscope.api.ShapeReader;
import io.github.fairdevkit.kaleidoscope.model.Shape;
import io.github.fairdevkit.kaleidoscope.model.ShapeGraph;
import io.shex.ShExDocLexer;
import io.shex.ShExDocParser;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

@ThreadSafe
public class ShExShapeReader implements ShapeReader {
    @Override
    public ShapeGraph read(InputStream source) throws IOException {
        var input = CharStreams.fromStream(source);
        var lexer = new ShExDocLexer(input);
        var tokens = new CommonTokenStream(lexer);
        var parser = new ShExDocParser(tokens);

        var schema = new Schema();
        var listener = new ShExParserListener(schema);

        ParseTreeWalker.DEFAULT.walk(listener, parser.shExDoc());

        return schema;
    }

    @Override
    public ShapeGraph read(InputStream source, @Nullable String base) throws IOException {
        return null;
    }

    @Override
    public Shape readShape(InputStream source, String subject) throws IOException {
        return null;
    }

    @Override
    public Shape readShape(InputStream source, String subject, @Nullable String base) throws IOException {
        return null;
    }
}
