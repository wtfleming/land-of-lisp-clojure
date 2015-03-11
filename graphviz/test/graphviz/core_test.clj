(ns graphviz.core-test
  (:require [clojure.test :refer :all]
            [graphviz.core :refer :all]))

(deftest dot-name-test
  (is (= (dot-name :living-room) "living_room"))
  (is (= (dot-name :foo!) "foo_"))
  (is (= (dot-name :24) "24")))

(deftest dot-label-test
  (is (= (dot-label {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."})
         "(living-room (You are in th...")))

(deftest node->dot-test
  (is (= (node->dot {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."})
         "living_room[label=\"(living-room (You are in th...\"];\n")))

(deftest nodes->dot-test
  (is (= (nodes->dot {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."
                      :garden "You are in a beautiful garden. There is a well in front of you"})
         "living_room[label=\"(living-room (You are in th...\"];\ngarden[label=\"(garden (You are in a beaut...\"];\n")))


(deftest edge->dot-test
  (is (= (edge->dot {:garden {:living-room {:east :door}}})
         ["garden->living_room[label=\"(east door)\"];\n"]))
  (is (= (sort (edge->dot {:living-room {:garden {:west :door}
                                         :attic {:upstairs :ladder}}}))
         ["living_room->attic[label=\"(upstairs ladder)\"];\n"
          "living_room->garden[label=\"(west door)\"];\n"]))
  (is (= (sort (edge->dot {:living-room {:garden {:west :door}
                                         :attic {:upstairs :ladder}}}))
         ["living_room->attic[label=\"(upstairs ladder)\"];\n"
          "living_room->garden[label=\"(west door)\"];\n"])))
