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
    :start-game ;; set state to "gather player"; sends the list of player for addition and verification. Deprecated: all users will need to be added one by one in the future
    {:db
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
                     {::players playersOrError}))}
    :complete-player-list ;; computes the indicative levels (max and recommended) and set state to "decide-settings"; 
    :add-player ;; Add a player to the game
    :furnish-player ;; Add additional information from the API to a Player
    :decide-settings ;; Records the settings, starts the downloading of the subjects, show the first kanji
    :exception ;; Ends the game and show an error message. Deprecated: We should be able to handle errors more gracefully in the future
    ))

(defn get-and-furnish-players [input-players]
  "Get players from the API and add them in the db.

  A player may be invalid if its API is not recognize by the Wanikani API. In that case, trigger an exception"
  ())
