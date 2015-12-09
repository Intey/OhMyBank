(ns ombs.view.actions
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]

    [ombs.dbold :as db]
    [ombs.db.payment :as dbp]

    [ombs.validate :as vld]
    [ombs.core :as core]
    [ombs.funcs :as fns]
    [ombs.view.dom :refer [set-attr-class rm-attr-class content-wrap]]
    ))

(defn pay [match]
  ((comp (h/set-attr :onclick "pay(this)") (h/content "Pay")) match))

(defn start [match]
  ((comp (h/set-attr :onclick "start(this)") (h/content "Start")) match))

(defn participate [match]
  ((comp (h/set-attr :onclick "participate(this)") (h/content "Participate")) match))

(h/defsnippet action-snip "../resources/public/event.html" [:.action]
  [{:keys [money button]} match]
  [:.money] (partial content-wrap money)
  [:button] button)

(defn get-button [button-type {:keys [name price date author status parts]}]
  (case button-type
    :pay          pay
    :start        start
    :participate  participate
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
                  :money price }

                 :else
                 {:button (get-button :participate event)
                  :money price })

      "in-progress" (if (core/participated? uname name date)
                      (if (dbp/can-pay? (db/get-uid uname) id)

                        {:button  (get-button :pay event)
                         :money (core/debt uname name date) }

                        {:button (h/content "Waiting...") })

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
