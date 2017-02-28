(ns ombs.user.db
  (:require
    [korma.db :as kdb]
    [korma.core :as sql]
    [ombs.funcs :as f]
    [ombs.db.old :refer :all]
            ))

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname :password password :bdate birthdate :rate rate } )))

(defn get-users []
  "Get usernames and their balances"
  (sql/select users))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn admin? [username]
  (->
    (sql/select users (sql/where {:name username :role admin-role-value}))
    first nil?  not))

(defn exists? [username]
  (not (empty? (first (sql/select users (sql/where {:name username}))))))
