(ns milkwood-clj.synthesise-test
  (:require [clojure.test :refer :all]
            [milkwood-clj.synthesise :refer :all]))

(deftest top-and-tail-test
  (testing "Test top and tailing of output"
    (is (= (top-and-tail '("a" "b" "c" "?" "d" "e" "f" "." "g" "h" "i" "!")) '("d" "e" "f" "." "g" "h" "i" "!")))
    (is (= (top-and-tail '("a" "b" "c" "?" "d" "e" "f" "." "g" "h" "i")) '("d" "e" "f" ".")))
    ))

(deftest write-output-test
  (testing "Test output to file"
    (is (= (do ;; (spit "test.out" "")
             (write-output '("Test" "output" ".") "test.out")
             (slurp "test.out"))) "Test output.")))
