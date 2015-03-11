(ns graphviz.core
  (:require [clojure.java.shell :as shell])
  (:gen-class))


(def wizard-nodes
  {:living-room "You are in the living-room. A wizard is snoring loudly on the couch."
   :garden "You are in a beautiful garden. There is a well in front of you"
   :attic "You are in the attic. There is a giant welding torch in the corner."})

(def wizard-edges
  {:living-room {:garden {:west :door}
                 :attic {:upstairs :ladder}}
   :garden {:living-room {:east :door}}
   :attic {:living-room {:downstairs :ladder}}})

(defn dot-name
  "Convert a node identifier into a valid DOT identifier"
  [exp]
  (clojure.string/replace (name exp) #"[^A-Za-z0-9_]" "_"))


(defn dot-label
  "Generate a label for a DOT. Truncate if longer than 30 characters."
  [node]
  (let [[k v] (first node)
        label (str "(" (name k) " (" v)]
    (if (> (.length label) 30)
      (str (subs label 0 27) "...")
      (str label ")"))))

(defn node->dot
  "Generate DOT into for a node"
  [node]
  (let [[k v] (first node)]
    (str (dot-name k) "[label=\"" (dot-label node) "\"];\n")))

(defn nodes->dot
   "Generate DOT info for a list of nodes"
   [nodes]
   (apply str (map #(node->dot (apply hash-map %)) nodes)))


(defn edge->dot
  "Generate DOT info for edges between nodes"
  [edge]
  (let [[k v] (first edge)
        src (dot-name k)]
    (loop [edges []
           exits v]
      (if (empty? exits)
        edges
        (let [exit (first exits)
              dest (dot-name (first exit))
              exit-direction (name (first (first (second exit))))
              exit-name (name (second (first (second exit))))
              msg (str src "->" dest "[label=\"(" exit-direction " " exit-name ")\"];\n")]
          (recur (conj edges msg) (rest exits)))))))

(defn edges->dot
  "Generate Dot info for the edges"
  [edges]
  (apply str (flatten (map #(edge->dot (apply hash-map %)) edges))))

(defn graph->dot
  "Generate DOT info for the graph"
  [nodes edges]
  (str "digraph{\n" (nodes->dot wizard-nodes) (edges->dot wizard-edges) "}\n"))

(defn dot->png!
  "Generate a png image of the game map"
  [f-name]
  (spit f-name (graph->dot wizard-nodes wizard-edges)))


(defn -main
  "Generate a png file representing the map of the wizard's adventure."
  [& args]
  (dot->png! "wizard.dot")
  (shell/sh "neato" "-Tpng" "wizard.dot" "-o" "wizard.png")
  ;; shell/sh is implemented with futures, cleanup
  (shutdown-agents))

