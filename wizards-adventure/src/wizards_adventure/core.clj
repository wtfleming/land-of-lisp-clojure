(ns wizards-adventure.core)

(def nodes
  {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."
   :garden "You are in a beautiful garden. There is a well in front of you"
   :attic "You are in the attic. There is a giant welding torch in the corner."})

(def edges
  {:living-room {:garden [:west :door]
                 :attic [:upstairs :ladder]}
   :garden {:living-room [:east :door]}
   :attic {:living-room [:downstairs :ladder]}})



(defn describe-location [location mnodes]
  (location mnodes))


(defn describe-path [exit-vec]
  "@param vector describing exits along the lines of [:west :door]"
  (let [[direction exit-type] exit-vec]
    (str "There is a " (name exit-type) " going " (name direction) " from here.")))


(defn describe-paths [location edges]  
  (map #(describe-path (second %))  (location edges)))
