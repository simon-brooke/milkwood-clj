(ns milkwood-clj.core
  (require [clojure.set :as set])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;;; utilities - probably in the fullness of time a separate file

;; copied verbatim from old (pre 1.3) clojure.contrib; cant find where it's moved
  ;; to in new contrib structure.
  ;; see https://github.com/clojure/clojure-contrib/blob/master/modules/map-utils/src/main/clojure/clojure/contrib/map_utils.clj
(defn deep-merge-with
  "Like merge-with, but merges maps recursively, applying the given fn
  only when there's a non-map at a particular level.

  (deep-merge-with + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
               {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
    (fn m [& maps]
      (if (every? map? maps)
        (apply merge-with m maps)
        (apply f maps)))
    maps))

;;;; read side - also probably a separate file

(defn compose-rule
  "Compose a new rule tree (containing (obviously) only one rule) from this path.

   path: a flat sequence of tokens."
  [path]
  (cond
   (empty? path) nil
   true (hash-map (first path) (compose-rule (rest path)))))


(defn merge-rules [these those]
  (deep-merge-with set/union these those))

(defn add-rule
  "Add the rule defined by this path to these rules.

   rules: a rule tree (i.e. a recursively nested map token => rule-tree);
   path: a flat sequence of tokens."
  [rules path]
  (cond
   ;; if we have no more path, we're done.
   (empty? path) nil
   ;; if we have no more rules, compose a rule from what's left of the path
   (empty? rules) (compose-rule path)
   ;; replace in the rules the rule for the first of the path, with this new
   ;; rule generated from the rest of the path and the old rule for the first
   ;; of the path.
   true (merge-rules rules (add-rule (rules (first path)) (rest path)))))


(defn read-rules
  "Read this stream and process it into rules.

  rules: a rule tree, which is to say a map which maps words onto rule trees (yes, it's recursive);
  anger: a lookback window, holding the last n tokens read, where n = depth;
  stream: an input stream from which we're reading;
  line: the last line read from the stream if any;
  depth: the depth of rules/length of window we're considering."
  [rules anger stream line depth]
  (cond
     ;; if line and stream are empty, we're done; return the rules.
        (empty? line) (cond (empty? stream) rules
                                ;; if only the line is empty, get a new line from the stream and carry on.
                                true (read-rules rules anger stream (read-line stream) depth))
        true (let [token (first line) rage (slide-window anger token depth)]
               ;; take the next token to consider off the front of the line and add it to the end of the
               ;; sliding window
               (cond
                  ;; if the new sliding window is deep enough, add a rule and continue.
                    (= (count rage) depth) (read-rules (add-rule rules rage) rage stream (rest line) depth)
                  ;; else just continue without adding a rule.
                    true (read-rules rules rage stream (rest line) depth))
    )))

;;;; write side


(defn slide-window
  "slide this lookback window. A lookback window is a list of at most depth tokens;
  we slide it by appending this token to its tail, and possibly removing a token from
  its head to make room. Oviously, we do this by compying, not by destructive
  modification.

  window: a flat sequence of tokens of length less than or equal to depth;
  token: a token to append;
  depth: the maximum length of the window."
  [window token depth]
  (let [newwindow (concat window (list token))]
    (cond
     (> (count newwindow) depth) (rest newwindow)
     true newwindow)))

(defn next-tokens
  "Given these rules and this path, return a list of valid next tokens to emit.

   rules: a rule tree (i.e. a recursively nested map token => rule-tree);
   path: a flat sequence of tokens."
  [rules path]
  (cond
   (empty? (rest path)) (shuffle (keys (rules (first path))))
   (empty? (rules (first path))) nil
   true (next-tokens (rules (first path)) (rest path))))

(defn compose-nonsense
  [window token depth length]
  (cond
   (= length 0) nil
   )

  )
