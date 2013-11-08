# milkwood-clj

Reimplementation of the Milkwood rule driven nonsense generator in Clojure.
See http://codekata.pragprog.com/2007/01/kata_fourteen_t.html

## Installation

Download from http://example.com/FIXME.

## Usage

    $ java -jar milkwood-clj-0.1.0-standalone.jar -f [filename] -d [depth]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

Not so much a bug, but as I've written this all as pure recursive functions it's vulnerable to stack exhaustion exceptions. I've specified extended stack size in the project file, but that won't be sufficient for analysing large texts.



## License

Copyright © 2013 Simon Brooke

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
