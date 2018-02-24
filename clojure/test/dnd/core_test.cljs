(ns dnd.core-test
  (:require [cljs.test :refer-macros  [deftest is]]
            [dnd.character]))

(deftest test-numbers
  (is  (= 1 1)))

(deftest test-strings
  (is  (= "yo" "yo")))

