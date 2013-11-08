(ns milkwood-clj.utils
  (require
   [clojure.set :as set])
  (:gen-class))

;;;; utilities - probably in the fullness of time a separate file


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
