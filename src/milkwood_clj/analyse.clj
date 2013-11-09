(ns milkwood-clj.analyse
  (require
   [milkwood-clj.utils :as utils]
   [clojure.set :as set])
  (:gen-class))

(def ^:const token-pattern
  "Regular expression used to split input into tokens.
   TODO: note that backslash-w captures underscores as word characters.
   Probably better to use [a-zA-Z]*."
;;  #"\w+\'[stdm]|\w+|\p{Punct}"
    #"\w+['-]\w+|\w+|\p{Punct}"
  )

(defn compose-rule
  "Compose a new rule tree (containing (obviously) only one rule) from this path.

   path: a flat sequence of tokens."
  [path]
  (cond
   (empty? path) nil
   true (hash-map (first path) (compose-rule (rest path)))))


(defn merge-rules
  "Merge two rule trees.

  these: a rule tree;
  those: a rule tree."
  [these those]
  (utils/deep-merge-with set/union these those))

(defn add-rule
  "Add the rule defined by this path to these rules.

   rules: a rule tree (i.e. a recursively nested map token => rule-tree);
   path: a flat sequence of tokens."
  [rules path]
  (merge-rules rules (compose-rule path)))

(defn analyse-tokens
  "Read this sequence of tokens and process it into rules.

  rules: a rule tree, which is to say a map which maps words onto rule trees (yes, it's recursive);
  anger: a lookback window, holding the last n tokens read, where n = depth;
  tokens: the sequence of tokens we're reading;
  depth: the depth of rules/length of window we're considering."
  [rules anger tokens depth]
  (cond
   (empty? tokens) rules
   true (let [token (first tokens) rage (utils/slide-window anger token depth)]
          ;; take the next token to consider off the front of the tokens and add it to the end of the
          ;; sliding window
          (cond
           ;; if the new sliding window is deep enough, add a rule and continue.
           (= (count rage) depth) (analyse-tokens (add-rule rules rage) rage (rest tokens) depth)
           ;; else just continue without adding a rule.
           true (analyse-tokens rules rage (rest tokens) depth)))))


(defn analyse-file
  "Read this file and process it into rules.

  file: the path name of a file to read;
  depth: the depth of rules/length of window we're considering"
  [file depth]
  (analyse-tokens nil nil
                   (re-seq token-pattern (slurp file)) depth))
