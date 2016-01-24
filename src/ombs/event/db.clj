(ns ombs.event.db
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.db.old :refer :all]
            [ombs.funcs :as f]
            ))


(def statuses {:initial "initial" :finished "finished"
               :in-progress "in-progress"})
(defn status-vector [ st ]
  (if (vector? st)
    (mapv #(% statuses) st)
    (st statuses)))
; (defrecord Event [name date price author status] )
; (defrecord PartialEvent [parts-count actual-parts])

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))

(defn add-event
  ([ename price author date & [parts participats]]
   (println (str "====== add event ==== :" ename ", " price ", " date ", " author ", " parts ", " participants))
   (println)
   (let [parts (if (nil? nil) 0 parts)]
     (-> (sql/insert events (sql/values
                              {:name ename :price price :author author :date
                               date :status (statuses :initial) :parts parts}))
         vals
         first))))

(defn set-status
  ([ename date s]
  (sql/update events (sql/set-fields {:status (statuses s)})
              (sql/where {:name ename :date date})))
  ([eid s]
  (sql/update events (sql/set-fields {:status (statuses s)})
              (sql/where {:id eid}))))

(declare subtract-feesed-parts)
(defn get-events
  ([] (get-events [:initial :in-progress :finished]))
  ([status]
   "Return list of events, and it actual count of event"
   ; Then we update each event elent: substract from each event count of parts,
   ; that hang in fees.
   ; Some events haven't parts, and after join it's have nil parts. So we need
   ; fix before substract.
   (map
     #(update % :rest
              (comp (partial subtract-feesed-parts (:id %)) f/nil-fix))
     ; First of all, we select events from it table and join for each
     ; rest(actual) parts.
     (sql/select events
                 (sql/fields :id :name :date :price :author :status :parts)
                 (sql/where {:status [in (status-vector status)]})
                 (sql/with goods (sql/fields :rest))))))

(defn subtract-feesed-parts [eid parts]
  "Substract from given parts, founded parts in active(all) fees."
  (assert (not= nil parts) "Can't subtract-feesed-parts from nil")
  (- parts
     (-> (sql/select fees (sql/fields :sum)
                     (sql/where {:events_id eid})
                     (sql/aggregate (sum :parts) :sum))
         (first)
         (:sum)
         (f/nil-fix))))

(defn get-active-events []
  (sql/select events
              (sql/where (not= :status (statuses :finished)))))

(defn get-event
  ([ename date] (get-event (get-eid ename date)))
  ([eid] (first (sql/select events (sql/where {:id eid})))))

(defn get-price
  ([ename date]
   (:price (first (sql/select events (sql/fields :price)
                              (sql/where (and (= :date date) (= :name ename)))))))
  ([eid]
   (:price (first (sql/select events (sql/fields :price) (sql/where {:id eid}))))))

(defn get-parts
  ([ename date]
   (f/nil-fix (:rest (first (sql/select goods (sql/fields :rest)
                                        (sql/where {:events_id (get-eid ename date)}))))))
  ([eid]
   (f/nil-fix (:rest (first (sql/select goods (sql/fields :rest)
                                        (sql/where {:events_id eid})))))
   ))

(defn get-status
  ([ename date] (get-status (get-eid ename date)))
  ([eid] (:status (first (sql/select events (sql/fields :status) (sql/where {:id eid}))))))

(defn is-initial?
  ([ename date] (is-initial? (get-eid ename date)))
  ([eid]
   (= (statuses :initial)
      (:status (first (sql/select events (sql/fields :status)
                                  (sql/where {:id eid} )))))))

(defn event-from-fee [fid]
  (first (sql/select events
                     (sql/with fees
                       (sql/fields)
                       (sql/where {:id fid})))))

(defn exists? [id] (not (empty? (get-event id))))

(defn delete [id]
      (sql/delete events (sql/where {:id id})))
