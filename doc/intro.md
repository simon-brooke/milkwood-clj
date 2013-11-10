# Implementing Milkwood in Java and Clojure

I was recently given, as a coding exercise by a potential employer, this [problem][] 
.

It's an interesting problem, because the set of N-grams (the problem specification suggests N=3, so trigrams, but I'm sufficiently arrogant that I thought it would be more interesting to generalise it) forms, in effect, a two dimensional problem space. We have to extend the growing tip of the generated text, the meristem, as it were; but to do so we have to search sideways among the options available at each point. Finally, if we fail to find a way forward, we need to back up and try again. The problem seemed to me to indicate a depth-first search. What we're searching is not an 'optimal' solution; there is no 'best' solutions. All possible solutions are equally good, so once one solution is found, that's fine.

## Data design

So the first issue is (as it often is in algorithmics) data design. Obviously the simpleminded solution would be to have an array of tuples, so the text:

    I came, I saw, I conquered.

would be encoded as

    I came I
    came I saw
    I saw I
    saw I conquered

The first thing to note is these tuples are rules, with the first N-1 tokens acting as the left hand side of the rule, and the last token acting as the right hand side:

    I came => I
    came I => saw
    I saw => I
    saw I => conquered

To be interpreted as 'if the last N-1 tokens I emitted match the left hand side of a rule, the right hand side of that rule is a candidate for what to emit next.'

The next thing to note is that if we're seeking to reconstruct natural language text with at least a persuasive verisimilitude of sense, punctuation marks are tokens in their own right:

    I came => COMMA
    came COMMA => I
    COMMA I => saw
    I saw => COMMA
    saw COMMA => I
    COMMA I => conquered
    I conquered => PERIOD

Now we notice something interesting. It's perfectly possible and legitimate to have two rules with the same left hand side, in this case {COMMA I}. So we could recast the two {COMMA I} rules as a single rule:

    COMMA I => [saw | conquered]

This means that, in our table of rules, each left-hand-side tuple can be distinct, which makes searching easier. However, a system which searches a table of N-ary tuples for matches isn't especially easy or algorithmically efficient to implement. If we had single tokens, we could easily use maps, which can be efficient. One can see at a glance that two tokens occur repeatedly in the first position of the left hand side of the rules, 'I', and 'COMMA'.

'I' has three possible successors:

    I [came | saw | conquered] 

However the right hand side is not the same for 'saw' as it is for conquered, so this composite rule becomes:

    I => [came => [COMMA]| saw => [COMMA]| conquered => [PERIOD]]

This enables us to consider our rules as a recursive map of maps:

    [
        I => [came => [COMMA]| saw => [COMMA]| conquered => [PERIOD]] |
        came => [COMMA => [I]] |
        COMMA => [I => [saw | conquered]] |
        saw => [COMMA => [I]]
    ]

And thus, essentially, as a tree that, given a path, we can walk. Matching becomes trivial and efficient.

Thus far we're almost language independent. I say almost, because in Prolog (which would be a very good implementation language for this problem) we'd simply assert all the N-grams as predicates and let the theorem solver sort them out. However, I've not (yet) tackled this problem in Prolog.

##Implementation: Java

My Java implementation is [milkwood-java][].

I started in Java, because that's what I was asked to do. Java (or C#, which is to a very close approximation the same language) is pretty much the state of the art as far as imperative, procedural languages go. Yes, I know it's object oriented, and I know Java methods are in principal functions not procedures. But it is still an imperative, procedural language. I say so, so it must be true. What I hope makes this essay interesting is that I then went on to reimplement in Clojure, so I can (and shall) compare and contrast the experience. I'm not (yet) an experienced Clojure hacker; I'm an old Lisp hacker, but I'm rusty even in Lisp, and Clojure isn't really very Lisp-like, so my Clojure version is probably sub-optimal.

But let's talk about Java. I made a tactical error early in my Java implementation which makes it less than optimal, too. We have an input file to analyse, and we don't know how big it is. So my first instinct wasn't to slurp it all into memory and then tokenise it there; my first instinct was to tokenise it from the stream, in passing. That should be much more conservative of store. And so I looked in the Java libraries, and there was a library class called StreamTokenizer. Obviously, that's what I should use, yes? Well, as I learned to my cost, no, actually. The class java.io.StreamTokenizer is actually part of the implementation of the Java compiler; it's not a general purpose tokeniser and adapting it to tokenise English wasn't wonderfully successful. That wasted a bit of time, and at the time of writing the Java implementation still depends on StreamTokenizer and consequently doesn't tokenise quite as I would like. If I backported the regex based tokeniser I used in the Clojure version to the Java version (which I easily could) it would be better.

So the first gotcha of Java was that the libraries now contain a lot of accreted crud.

The second point to note about Java is how extraordinarily prolix and bureaucratic it is. My Java implementation runs to almost a thousand lines, of which over 500 lines are actual code (317 comment lines, 107 blank lines, 36 lines of import directives). Now, there are two classes in my solution, Window and WordSequence, which could possibly be refactored into one, saving a little code. But fundamentally it's so large because Java is so prolix.

By contrast, the Clojure reimplementation, which actually does more, is a third the size - 320 lines, of which 47 are blank and 29 are inline comments. I don't yet have a tool which can analyse Clojure documentation comments, but at a guess there's at least fifty lines of those, so the Clojure solution is no more than two fifths of the size of the Java.

The Java implementation comprises eight classes:

* **Composer** essentially the two mutually recursive functions which perform depth first search over the rule set, to compose output
* **Digester** scans a stream of text and composes from it a tree of rules
* **Milkwood** contains the main() method; parses command line arguments
* **RuleTreeNode** a node in the tree of rules
* **Tokeniser** a wrapper around StreamTokenizer, to try to get it to tokenise English; not very successful
* **Window** a fixed length stack of tokens, used as a glance-back window in both scanning and composing
* **WordSequence** a sequence of tokens implemented as a queue
* **Writer** a wrapper around BufferedWriter which performs on-the-fly orthographic tricks to create a verisimilitude of natural English

One might argue that that's excessive decomposition for such a small problem, but actually small classes greatly increase the comprehensibility of the code.

There are things I'm not proud of in the Java implementation and I may at some stage go back and polish it more, but it isn't a bad Java implementation and is fairly representative of the use of Java in practice.

## Clojure implementation

My Clojure implementation is [milkwood-clj][].

Some things to say about the Clojure implementation before I start. First, I implemented it in my own time, not under time pressure. Second, although I'm quite new to Clojure, I'm an old Lisp hacker, and even when I'm writing Java there are elements of Lisp-style in what I write. Thirdly, although I'm trying to write as idiomatic Clojure as I'm able, because that's what I'm trying to learn, I am a Lisp hacker at heart and consequently use **cond** far more than most Clojure people do - despite the horrible bastardised mess Clojure has made of **cond**. Finally, it was written after the Java implementation so I was able to avoid some of the mistakes I'd made earlier.

I used [LightTable][] as my working environment. I really like the ideas behind LightTable and suspect that in time it will become my IDE of choice, but I haven't got it working for me yet. Particularly I haven't got its 'documentation at cursor' function working, which, given my current (lack of) familiarity with the Clojure, is a bit of a nuisance.

I tripped badly over one thing. Clojure, to my great surprise, does not support mutually recursive functions, and the algorithm I'd designed depends crucially on mutually recursive functions. However after a bit of flailing around, I remembered it does support dispatch in one function on different arities of arguments, and I was able to rewrite my two functions as different arity branches of the same function, which then compiled without difficulty.

The other trip was that **map**, in Clojure, is lazy. So when I tried to write my output using

    (defn write-output
        "Write this output, doing little orthographic tricks to make it look superficially
         like real English text.
 
         output: a sequence of tokens to write."
        [output]
        (map write-token output))

nothing at all was printed, and I couldn't understand why not. The solution is that you have to wrap that **map** in a call to **dorun** to force it to evaluate.

Aside from that, writing in Clojure was a total joy. Being able to quickly test ideas in a **repl** ('Read Eval Print Loop') is a real benefit. But a clean functional language is so simple to write in, and data structures are so easy to build and walk.

Another thing Clojure makes much easier is unit tests. I got bogged down in the mutual recursion part of the Java problem and unit tests would have helped me - but I didn't write them because the bureaucratic superstructure is just so heavy. Writing unit tests should be a matter of a moment, and in Clojure it is. 

I broke the Clojure implementation into four files/namespace:

* **analyse.clj** read in the input and compile it into a rule tree; more or les Tokeniser and Digester in [milkwood-java][];
* **core.clj** essentially replaces Milkwood in [milkwood-java][]; parses command line arguments and kicks off the process;
* **synthesise.clj** compose and emit the output; broadly equivalent to Composer and Writer in [milkwood-java][];
* **utils.clj** small utility functions. Among other things, contains the equivalent of Window in [milkwood-java][].

Additionally there are two test files, one each for analyse and synthesise, containing in total seven tests with eight assertions. Obviously this is not full test coverage; I wrote tests to test specific functions which I was uncertain about.

## Conclusion

Obviously, all Java's bureaucracy does buy you something. It's a *very* strongly typed language; you can't (or at least it's very hard to) just pass things around without committing to exactly what they will be at compile time. That means that many problems will be caught at compile time. By contrast, many of the functions in my Clojure implementation depend on being passed suitable values and will break at run time if the values passed do not conform. 

Also, of course, the JVM is optimised for Java. I've blogged quite a bit about [optimising the JVM for functional languages][]; but, in the meantime, my Java implementation executes about seven times as fast as my Clojure implementation (but I'm timing from the shell and I haven't yet instrumented how long the start up time is for Java vs Clojure). Also, of course, I'm not an experienced Clojure hacker and some of the things I'm doing are very inefficient; [Alioth's Clojure/Java figures][] suggest much less of a performance deficit. But if peformance is what critically matters to you, it seems to me that probably the performance of Java is better, and you at least need to do some further investigation.

On the other hand, at bottom Java is fundamentally an Algol, which is to say it's fundamentally a bunch of hacks constructed around things people wanted to tell computers to do. It's a very developed Algol which has learned a great deal from the programming language experience over fifty years, but essentially it's just engineering. There's no profound underlying idea.

Clojure, on the other hand, is to a large extent pure Lambda calculus. It is much, much more elegant. It handles data much more elegantly. It is for me much more enjoyable to write.

[problem]: http://codekata.pragprog.com/2007/01/kata_fourteen_t.html "The problem specification"
[LightTable]: http://www.lighttable.com/ "The IDE of the future?"
[milkwood-java]: https://github.com/simon-brooke/milkwood-java "Milkwood in Java"
[milkwood-clj]: https://github.com/simon-brooke/milkwood-clj "Milkwood in Clojure"
[optimising the JVM for functional languages]: http://blog.journeyman.cc/search/label/Memory%20management "Essays on memory management"
[Alioth's Clojure/Java figures]: http://benchmarksgame.alioth.debian.org/u64q/clojure.php "Computer Language Benchmarks Game"

