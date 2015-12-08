(ns ombs.view.event
  (:require
    [net.cgrand.enlive-html :as h]
    [net.cgrand.reload :as reload]
    [noir.session :as sess]
    [ombs.validate :as vld]
    [ombs.core :as core]
    [ombs.funcs :as fns]
    [ombs.dbold :as db]
    [ombs.validate :refer [errors-string]]
    [ombs.view.dom :refer [set-attr-class rm-attr-class content-wrap]]
    [ombs.view.actions :as actions]
    ))

(h/defsnippet action "../resources/public/event.html" [:.action] [{:keys [money button]} match]
  [:.money] (partial content-wrap money)
  [:button] button)

(declare get-action)
(h/defsnippet event-elem "../resources/public/event.html" [:.event] [{:keys [id name price date author status parts] :as event}]
  [:.event]  (h/set-attr :id id)
  [:.name]   (h/content (str author "'s " name))
  [:.date]   (h/content (str date))
  [:.action] (partial action (get-action event))
  )

(defn get-button [button-type {:keys [name price date author status parts]}]
  (println button-type)
  (case button-type
    :pay          actions/pay
    :start        actions/start
    :participate  actions/participate
    :finished     (partial content-wrap "Finished")
    )
  )

(defn get-action [{:keys [id name price date author status parts] :as event}]
  "Return hashmap with function for money and button"
  (if-let [uname (sess/get :username)]
    (case status
      "initial" (cond
                 (and (= uname author)
                      (> (core/participants-count name date) 0))
                 {:button (get-button :start event)
                  :money price }

                 (core/participated? uname name date)
                 {:button nil
                  :money price}

                 :else
                 {:button (get-button :participate event)
                  :money price}
                 )

      "in-progress" (if (core/participated? uname name date)
                      (if (core/fee-exist? id (db/get-uid uname))

                        {:button (h/content "Waiting...") }

                        {:button  (get-button :pay event)
                         :money (core/debt uname name date)})

                     {:button  (get-button :participate event)
                      :money (fns/part-price
                               price (core/participants-count name date))}
                     )

      "finished" {
                  :button (get-button :finished event)
                  :money price
                  }
      )
    (vld/add-error :broken-session "Session is broken. Relogin please.")
    ))

(reload/auto-reload *ns*)
