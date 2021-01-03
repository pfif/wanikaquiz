(ns wanikaquiz.core
  (:require
   [clojure.core.async :as a]
   [wanikaquiz.gameloop :refer [gameloop-channel-out gameloop-channel-in start-gameloop]]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [wanikaquiz.events :as events]
   [wanikaquiz.views :as views]
   [wanikaquiz.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(re-frame/reg-event-db
 :change-name
 (fn [db name]
   (assoc db :name name)))

(re-frame/reg-event-fx
 :reset-counter
 (fn [_ _]
   {:ask-reset-counter nil}))

(re-frame/reg-fx
 :ask-reset-counter
 (fn [_]
   (a/go
     (a/>! gameloop-channel-in 0))))

(defn start-reading-from-gameloop []
  (a/go
    (a/go-loop []
      (re-frame/dispatch [:change-name (a/<! gameloop-channel-out)])
      (recur))
    ))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (start-gameloop)
  (start-reading-from-gameloop)
  (mount-root))
