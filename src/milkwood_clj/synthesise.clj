(ns milkwood-clj.synthesise
  (require
   [milkwood-clj.utils :as utils])
  (:gen-class))


(def end-magic-token
  "A token to mark the end of the generated test, used to
  distinguish completion from failure."
  "ENDMAGICTOKEN")

(defn next-tokens
  "Given these rules and this path, return a list of valid next tokens to emit.

   rules: a rule tree (i.e. a recursively nested map token => rule-tree);
   path: a flat sequence of tokens."
  [rules path]
  (cond
   (empty? (rest path)) (keys (rules (first path)))
   (empty? (rules (first path))) nil
   true (next-tokens (rules (first path)) (rest path))))

(defn compose-prologue
   "Generate a prologue before entering the rule driven part of the synthesis
    process. The prologue needs to be as long as the rule-tree depth, to allow
    it to be used as a glance back window.
    TODO: Should really generate something which is a gramatically legal start
    of an English sentence.

    rules: a rule tree;
    depth: the depth of that rule tree."
  [rules depth]
  (cond
   (= depth 0) nil
   true (let [token (first (shuffle (keys rules)))]
          (cons token (compose-prologue (rules token) (- depth 1))))))



(defn compose-nonsense
  "Try to compose this much nonsense given these rules and this glance-back window.
   This is a rather messy hack around Clojure's curiously awkward rules on mutually
   recursive functions. The second and third arities of this function (4 args and
   5 args respectively) are in effect mutually recursive functions. The 4 args form
   extends the output, the five args form hunts sideways among the options. The first,
   two arg arity is simply a convenient entry point.

   rules: a rule tree;
   window: a glance back at the last tokens emitted;
   depth: the target size of the glance back window, which should be one less than
          the depth of the rule tree;
   length: the number of tokens of nonsense which should be generated;
   options: candidates to be the next token"
  ([rules length]
   (compose-nonsense rules nil (- (utils/rule-tree-depth rules) 1) length))
  ([rules window depth length]
  (let [window-size (count window) options (next-tokens rules window)]
    (cond
      ;; if no more length, we're done. We need to return something non-null to indicate we haven't failed.
     (= length 0) (list end-magic-token)
      ;; if we've no window, we need to choose one and continue.
     (= window nil)
     (let [prologue (compose-prologue rules depth)]
       (flatten (list prologue (compose-nonsense rules prologue depth (- length depth)))))
      ;; if we've a window but no options, we're stuffed.
     (= options nil) nil
     true (compose-nonsense rules window depth (- length 1) (shuffle options)))))
  ([rules window depth length options]
    (cond
     ;; if I've run out of options, I've failed.
     (empty? options) nil
     ;; if I still have some options...
     true
     ;; try the first of them...
       (let [nonsense (compose-nonsense rules (utils/slide-window window (first options) depth) depth length)]
         (cond
          ;; if that fails, try the others...
          (empty? nonsense) (compose-nonsense rules window depth length (rest options))
          ;; but if it succeeds we're good.
          true (cons (first options) nonsense))
         ))))

(defn write-token
  [token]
  "Write a single token to the output, doing some basic orthographic tricks.

   token: the token to write."
  (cond
   (= token end-magic-token) nil
   (re-find #"^[.!?]$" token) (do (print token) (cond (= (rand-int 5) 0) (print "\n\n")))
   (re-find #"^\p{Punct}$" token) (print token)
   true (print (str " " token))))

(defn write-output
  "Write this output, doing little orthographic tricks to make it look superficially
   like real English text.
   TODO: does not yet work. Should take an optional second argument,
      the file to write to if any (default to standard out).

   output: a sequence of tokens to write."
  [output]
  (dorun (map write-token output)))
