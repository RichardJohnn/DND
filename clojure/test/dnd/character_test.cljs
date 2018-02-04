(ns dnd.character-test
  (:require [cljs.test :refer-macros  [deftest is]]
            [dnd.character]))

(deftest test-things
  (is  (= (dnd.character/character-direction 0 1) "s"))
  (is  (= (dnd.character/character-direction 0 -1) "n"))
  (is  (= (dnd.character/character-direction 1 0) "e"))
  (is  (= (dnd.character/character-direction -1 0) "w")))

