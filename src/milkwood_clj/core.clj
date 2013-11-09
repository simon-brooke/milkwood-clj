(ns milkwood-clj.core
  (require
   [milkwood-clj.analyse :as analyse]
   [milkwood-clj.synthesise :as synthesise]
   [clojure.set :as set])
  (:use [clojure.tools.cli :only [cli]])
  (:gen-class))


(defn -main
  "Parse command line arguments and kick off the process."
  [& args]
  (let [[arguments _ banner] (cli args ["-f" "--file" "The path name of the file to analyse (string)"]
                       ["-l" "--output-length"
                        "The length in tokens of the output to generate (integer)"
                        :parse-fn #(Integer. %)
                        :default 100]
                       ["-h" "--help" "Print this text and exit" :flag true]
                       ["-o" "--output" "The path name of the file to write to, if any (string)"]
                       ["-t" "--tuple-length"
                        "The length of the sequences to analyse it into (integer)"
                        :parse-fn #(Integer. %)
                        :default 2]) file (arguments :file)]
    (cond
     (= file nil) (print banner)
     (arguments :help) (print banner)
     true (synthesise/write-output
           (synthesise/compose-nonsense
            (analyse/analyse-file file (arguments :tuple-length))
            (arguments :output-length))))
    (prn "\n")))


