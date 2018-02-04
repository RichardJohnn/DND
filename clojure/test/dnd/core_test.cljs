(ns dnd.core-test
  (:require [cljs.test :refer-macros  [deftest is testing]]
            [dnd.character]))

(deftest test-numbers
  (is  (= 1 1)))

(deftest test-strings
  (is  (= "yo" "yo")))

(deftest test-things
  (is  (= (dnd.character/character-direction 0 1) "s")))
