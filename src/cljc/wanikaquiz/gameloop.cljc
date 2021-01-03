(ns wanikaquiz.gameloop
  (:require
   [clojure.core.async :as a]))

(def gameloop-channel-out (a/chan))
(def gameloop-channel-in (a/chan))

(def current-value (atom 5))

(defn start-gameloop []
  (a/go-loop []
    (a/<! (a/timeout 1000))
    (swap! current-value inc)
    (a/>! gameloop-channel-out @current-value)
    (recur)
    )
  (a/go-loop []
    (a/<! gameloop-channel-in)
    (reset! current-value 0)
    (a/>! gameloop-channel-out 0)
    (recur)
    )
  )
