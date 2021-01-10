(ns wanikaquiz.gameloop
  (:require
   [clojure.spec.alpha :as sp]))

(sp/def ::username (sp/and string? not-empty))
(sp/def ::apikey (sp/or :key string? :nokey nil?))
(sp/def ::player (sp/keys :req-un [::username ::apikey]))

(sp/def ::error string?)
(sp/def ::errorMap (sp/keys :req [::error]))

(defn gameloop [db event options]
  (case event
    :start-game {:db
                 (let [playersOrError (reduce (fn [acc player]
                                                (if (sp/valid? ::player player)
                                                  (conj acc
                                                        {:username (:username player) :apikey (let [key (:apikey player)]
                                                                                                (if (or (= key nil) (= key ""))
                                                                                                  nil
                                                                                                  {:key key :verified? :inprogress}))})
                                                  (reduced {::error "Uncomform player"})))
                                              []
                                              (:players options))]
                   (if (sp/valid? ::errorMap playersOrError)
                     playersOrError
                     {::players playersOrError}))}))
