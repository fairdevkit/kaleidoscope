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
package io.github.fairdevkit.kaleidoscope.shex

import org.eclipse.rdf4j.model.util.Values
import spock.lang.Specification

class ShExShapeReaderSpec extends Specification {
    /** System under test */
    def reader = new ShExShapeReader()

    // convenience closures
    def read = { data ->
        println "DBG: got shex data"
        println data
        println "end DBG"
        def stream = new ByteArrayInputStream(data.bytes)
        reader.read(stream)
    }
    def shex = { ...statements ->
        ["PREFIX ex: <http://example.com/> ", *statements].join('\n')
    }
    def ns = Values.&namespace

    def cleanup() {
        println "-"*80
    }

    def "namespaces are read and set in the schema"() {
        when:
        def schema = read "PREFIX $prefix: <$namespace>"

        then:
        schema.namespaces == [ ns(prefix, namespace) ]

        where:
        prefix | namespace
        "ex"   | "http://example.com"
        ""     | "http://foo"
    }

    def "test"() {
        when:
        def schema = read shex(
                "ex:Example {",
                "  ex:value LITERAL",
                "}")

        then:
        notThrown Exception
    }

    def "test regex node constraint at shape expression"() {
        when:
        def schema = read shex("ex:Example IRI /^http:\\/\\/example\\.com\\/[0-9]+/ {}")

        then:
        notThrown Exception
    }

    def "test regex triple constraint at triple expression"() {
        when:
        def schema = read shex(
                "ex:Example {",
                "  ex:value IRI /^http:\\/\\/example\\.com\\/[0-9]+/",
                "}"
        )

        then:
        notThrown Exception
    }

    def "test literal facet node constraint at shape expression"() {
        when:
        def schema = read shex(
                "ex:Example LITERAL MinInclusive 5 {",
                "}"
        )

        then:
        notThrown Exception
    }

    def "test primer quickstart example"() {
        when:
        def schema = read """\
            PREFIX ex: <http://ex.example/#>
            PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
            PREFIX school: <http://school.example/#>
            PREFIX foaf: <http://xmlns.com/foaf/0.1/>
            
            school:enrolleeAge xsd:integer MinInclusive 13 MaxInclusive 20 
            
            school:Enrollee {
              foaf:age @school:enrolleeAge ;
              ex:hasGuardian IRI {1,2}
            }
            """.stripIndent()

        then:
        notThrown Exception
    }
}
