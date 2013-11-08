(ns milkwood-clj.analyse-test
  (:require [clojure.test :refer :all]
            [milkwood-clj.analyse :refer :all]))

(deftest flat-rule-test
  (testing "Create a rule set with a single rule"
    (is (= (add-rule nil '("a" "b" "c" "d")) '{"a" {"b" {"c" {"d" nil}}}}))))

(deftest two-distinct-rule-test
  (testing "Create a set of two rules with no common tokens"
    (is
     (=
      (add-rule (add-rule nil '("a" "b" "c" "d")) '("a" "b" "e" "f"))
      '{"a" {"b" {"c" {"d" nil} "e" {"f" nil}}}}))))

(deftest two-fork-rule-test
  (testing "Create a set of two rules with common initial tokens"
    (is
     (=
      (add-rule (add-rule nil '("a" "b" "c" "d")) '("p" "q" "r" "s"))
      '{"a" {"b" {"c" {"d" nil}}} "p" {"q" {"r" {"s" nil}}}}))))

(deftest merge-two-rules
  (testing "Merge two rule trees each comprising a single rule, with no tokens common between trees"
    (is
     (=
      (merge-rules
       (add-rule nil '("a" "b" "c" "d"))
       (add-rule nil '("p" "q" "r" "s")))
      '{"a" {"b" {"c" {"d" nil}}} "p" {"q" {"r" {"s" nil}}}}))))

(deftest merge-fork-rules
  (testing "Merge two rule tees each comprising a single rule, with common initial tokens"
    (is
     (=
      (merge-rules
       (add-rule nil '("a" "b" "c" "d"))
       (add-rule nil '("a" "b" "e" "f")))
      '{"a" {"b" {"c" {"d" nil} "e" {"f" nil}}}}))))
