(ns untitled.Task_2)

(defn add [sieveMap next step]
  (let [num (+ next step)]
    (if (sieveMap num)
      (recur sieveMap num step)
      (assoc sieveMap num step))))

(defn tryGet [sieveMap next]
  (if-let [step (sieveMap next)]
    (-> (dissoc sieveMap next)
        (add next step))
    (add sieveMap next (+ next next))))

(defn getPrimes [sieveMap next]
  (if (sieveMap next)
    (getPrimes (tryGet sieveMap next) (+ 2 next))
    (cons next (lazy-seq (getPrimes (tryGet sieveMap next) (+ 2 next))))))


;(print (->> (getPrimes {} 3)
;            (concat [2])
;            (take 3)
;            )
;       )

(print (add {} 7 2))