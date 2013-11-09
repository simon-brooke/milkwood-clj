# milkwood-clj

Reimplementation of the Milkwood rule driven nonsense generator in Clojure.
See http://codekata.pragprog.com/2007/01/kata_fourteen_t.html

## Installation

Clone from https://github.com/simon-brooke/milkwood-clj and build with Lieningen

## Usage

    $ java -jar milkwood-clj-0.1.0-standalone.jar -f [filename] -d [depth]

## Options

    -f, --file                      The path name of the file to analyse (string)
    -l, --output-length    100      The length in tokens of the output to generate (integer)
    -h, --no-help, --help  false    Print this text and exit
    -o, --output                    The path name of the file to write to, if any (string)
    -t, --tuple-length     2        The length of the sequences to analyse it into (integer)

## Examples

    simon@engraver:~/workspace/milkwood-clj$ java -Xss4m -jar target/milkwood-clj-0.1.0-SNAPSHOT-standalone.jar -f ../milkwood/undermilkwood.txt -t 4 -l 100

 ears, conjures MRS ORGAN MORGAN And then Palestrina, SECOND VOICE he shouts to his deaf dog who smiles and licks his hands. UTAH WATKINS Gallop, you bleeding cripple! FIRST VOICE From where you are you can hear in Cockle Row in the spring, moonless night, Miss Price, in my pretty print housecoat, deft at the clothesline, natty as a jenny-wren, then pit-pat back to my egg in its cosy, my crisp toast-fingers, my home-made plum and butterpat.

### Bugs

Not so much a bug, but as I've written this all as pure recursive functions it's vulnerable to stack exhaustion exceptions. I've specified extended stack size in the project file, but that won't be sufficient for analysing large texts.

## License

Copyright © 2013 Simon Brooke

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
