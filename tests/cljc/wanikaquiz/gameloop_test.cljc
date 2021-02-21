(ns wanikaquiz.gameloop-test
  (:require
   [clojure.test :as t]
   [wanikaquiz.gameloop :as gameloop]
   [clojure.string :as s]))

(t/deftest gameloop
  (let [table [{:event :start-game,
                :name "1 player",
                :initialdb {},
                :arguments {:players [{:username "pfif" :apikey "xxxx"}]},
                :expectedEffects {:db {:wanikaquiz.gameloop/players [{:username "pfif" :apikey {:key "xxxx" :verified? :inprogress}}]}}}
               {:event :start-game,
                :name "2 player",
                :initialdb {},
                :arguments {:players [{:username "pfif" :apikey "xxxx"} {:username "friend" :apikey "yyyy"}]},
                :expectedEffects {:db {:wanikaquiz.gameloop/players [{:username "pfif" :apikey {:key "xxxx" :verified? :inprogress}}
                                                                     {:username "friend" :apikey {:key "yyyy" :verified? :inprogress}}]}
                                  :fx [[:wanikaquiz.gameloop/get-and-furnish-players [{:username "pfif" :apikey "xxxx"}
                                                                                      {:username "friend" :apikey "yyyy"}]]
                                       [:dispatch [:wanikaquiz.gameloop/player-list-completed]]]}}
               {:event :start-game,
                :name "1 player (one missing username)", ;; TODO Should filter out incomplete player instead of erroring
                :initialdb {},
                :arguments {:players [{:username "" :apikey "xxxx"}]},
                :expectedEffects {:db {:wanikaquiz.gameloop/error "Uncomform player"}}}
               {:event :start-game,
                :name "1 player (one without api key)",
                :initialdb {},
                :arguments {:players [{:username "pfif" :apikey nil}]},
                :expectedEffects {:db {:wanikaquiz.gameloop/players [{:username "pfif" :apikey nil}]}}}
               {:event :start-game,
                :name "1 player (one with empty api key)",
                :initialdb {},
                :arguments {:players [{:username "pfif" :apikey ""}]},
                :expectedEffects {:db {:wanikaquiz.gameloop/players [{:username "pfif" :apikey nil}]}}}]]

    (doseq [test table]
      (t/testing (s/join "" [(:event test) (:name test)])
        (t/is (= (:expectedEffects test)
                 (gameloop/gameloop (:initialdb test) (:event test) (:arguments test))))))))
