(ns milkwood-clj.synthesise
  (require
   [milkwood-clj.utils :as utils])
  (:gen-class))


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
