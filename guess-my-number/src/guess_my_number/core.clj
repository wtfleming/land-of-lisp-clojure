(ns guess-my-number.core)

(def small (atom 1))
(def big (atom 100))

(defn guess-my-number []
  (bit-shift-right (+ @small @big) 1))

(defn start-over []
  (reset! small 1)
  (reset! big 100)
  (guess-my-number))

(defn smaller []
  (reset! big (- (guess-my-number) 1))
  (guess-my-number))

(defn bigger []
  (reset! small (+ (guess-my-number) 1))
  (guess-my-number))
