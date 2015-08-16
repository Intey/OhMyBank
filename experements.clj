(def users {
            :student1 { :rate (rand-rate) :name "Betty"}
            :worker01 { :rate (rand-rate) :name "Mark"}
            :student2 { :rate (rand-rate) :name "Joe" }
            :worker02 { :rate (rand-rate) :name "Chend ler"}})

(defn get-rate [user]
  (:rate
   (val user)))
(get-rate (first users))

(defn rates-sum [users] 
  (let [rates (map get-rate users)]
  (reduce + rates)))

(rates-sum users)
  
; (reduce #(+ (get-rate %1)) 0 users) ; gets summ of rate from map of map
