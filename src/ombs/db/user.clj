(ns ombs.db.user
  (:require
    [korma.db :as kdb]
    [korma.core :as sql]
    [ombs.funcs :as f]
    [ombs.db.old :refer :all]
            ))

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname :password password :bdate birthdate :rate rate } )))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select balances (sql/where (= :name uname))
                     (sql/limit 1))))

(defn get-users []
  "Get usernames and their balances"
  (sql/select balances (sql/where (< 0 :balance))))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn get-rate [uname]
  (:rate (first (sql/select users (sql/fields :rate)
                            (sql/where (= :name uname))))))

(defn get-usernames [] (sql/select users (sql/fields :name)))

(defn get-rates [usernames] (map #(get-rate %) usernames))

(defn admin? [username] (->
                          (sql/select users (sql/where {:name username :role admin-role-value}))
                          first nil?  not))

(defn exists? [username]
  (not (empty? (first (sql/select users (sql/where {:name username}))))))
