(ns wizards-adventure.core
  (:require [clojure.string :as str]))

(def nodes
  {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."
   :garden "You are in a beautiful garden. There is a well in front of you"
   :attic "You are in the attic. There is a giant welding torch in the corner."})

(def edges
  {:living-room {:garden [:west :door]
                 :attic [:upstairs :ladder]}
   :garden {:living-room [:east :door]}
   :attic {:living-room [:downstairs :ladder]}})

(def objects [:whiskey :bucket :frog :chain])

(def object-locations (atom
                       {:whisky :living-room
                        :bucket :living-room
                        :chain :garden
                        :frog :garden}))

(def location (atom :living-room))

(def allowed-commands #{:look :walk :pickup :inventory})


(defn describe-location [loc mnodes]
  [(loc mnodes)])


(defn describe-path [exit-vec]
  "@param vector describing exits along the lines of [:west :door]"
  (let [[direction exit-type] exit-vec]
    (str "There is a " (name exit-type) " going " (name direction) " from here.")))

(defn describe-paths [location edges]  
  (map #(describe-path (second %))  (location edges)))


(defn objects-at [location object-locations]
  (map first (location (group-by second object-locations))))

(defn describe-object [object]
 (str "You see a " (name object) " on the floor."))

(defn describe-objects [location object-locations]
  (map describe-object (objects-at location object-locations)))


(defn edge-in-direction [loc direction edges]
   (for [x (loc edges)
         :when (some #{direction} (second x))]
     (first x)))


;;; In game Commands

(defn look []
  (str/join " "
            (concat
             (describe-location @location nodes)
             (describe-paths @location edges)
             (describe-objects @location @object-locations))))

 (defn walk [direction]
   (let [edge (edge-in-direction @location direction edges)]
     (if (empty? edge)
         "You cannot go that way."
         (do
           (reset! location (first edge))
           (look)))))
       
(defn pickup [object]
  (if (some #{object} (objects-at @location @object-locations))
    (do
      (swap! object-locations assoc object :body)
      (str "You are now carrying the " (name object)))
    "You can not get that."))

(defn inventory []
  (let [objects (objects-at :body @object-locations)]
    (if (empty? objects)
        "You are not carrying anything."
        (str "Items: " (str/join ", " (map name objects))))))


;;; Game repl

(defn game-read []
  (let [command (read-string (str "(" (read-line) ")"))]
    (cons
     (first command)
     (map #(keyword %) (rest command)))))


(defn game-eval [sexp]
  (let [cmd (keyword (first sexp))]
    (if (get allowed-commands cmd)
      (eval sexp)
      "I do not know that command.")))


(defn game-repl []
  (let [full-command (game-read)
        cmd (keyword (first full-command))]
    (when-not (= cmd :quit)
      (println (game-eval full-command))
      (recur))))

