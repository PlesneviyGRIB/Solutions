(ns Task_4
  (:import (java.text SimpleDateFormat)
           (java.util Date)))

(declare supply-msg)
(declare notify-msg)

(defn storage
  "Creates a new storage
   ware - a name of ware to store (string)
   notify-step - amount of stored items required for logger to react. 0 means to logging
   consumers - factories to notify when the storage is updated
   returns a map that contains:
     :storage - an atom to store items that can be used by factories directly
     :ware - a stored ware name
     :worker - an agent to send supply-msg"
  [ware notify-step & consumers]
  (let [counter (atom 0 :validator #(>= % 0)),
        worker-state {:storage     counter,
                      :ware        ware,
                      :notify-step notify-step,
                      :consumers   consumers}]
    {:storage counter,
     :ware    ware,
     :worker  (agent worker-state)}))

(defn factory
  "Creates a new factory
   amount - number of items produced per cycle
   duration - cycle duration in milliseconds
   target-storage - a storage to put products with supply-msg
   ware-amounts - a list of ware names and their amounts required for a single cycle
   returns a map that contains:
     :worker - an agent to send notify-msg"
  [amount duration target-storage & ware-amounts]
  (let [bill (apply hash-map ware-amounts),
        buffer (reduce-kv (fn [acc k _] (assoc acc k 0))
                          {} bill),
        worker-state {:amount         amount,
                      :duration       duration,
                      :target-storage target-storage,
                      :bill           bill,
                      :buffer         buffer}]
    {:worker (agent worker-state)}))

(defn source
  "Creates a source that is a thread that produces 'amount' of wares per cycle to store in 'target-storage'
   and with given cycle 'duration' in milliseconds
   returns Thread that must be run explicitly"
  [amount duration target-storage]
  (new Thread
       (fn []
         (Thread/sleep duration)
         (send (target-storage :worker) supply-msg amount)
         (recur))))

(defn supply-msg
  "A message that can be sent to a storage worker to notify that the given 'amount' of wares should be added.
   Adds the given 'amount' of ware to the storage and notifies all the registered factories about it
   state - see code of 'storage' for structure"
  [state amount]
  (swap! (state :storage) #(+ % amount))                    ;update counter, could not fail
  (let [ware (state :ware),
        cnt @(state :storage),
        notify-step (state :notify-step),
        consumers (state :consumers)]
    (when
      (and (> notify-step 0)
               (> (int (/ cnt notify-step))
                  (int (/ (- cnt amount) notify-step))))
      (println "[" (.format (new SimpleDateFormat "hh.mm.ss.SSS") (new Date)) "]" ware "amount =" cnt))
    (when consumers
      (doseq [consumer (shuffle consumers)]
        (send (consumer :worker) notify-msg ware (state :storage) amount))))
  state)                                                    ;worker itself is immutable, keeping configuration only

(defn notify-msg
  "A message that can be sent to a factory worker to notify that the provided 'amount' of 'ware's are
   just put to the 'storage-atom'."
  [state ware storage-atom amount]
  (let [bill (state :bill),
        buffer (state :buffer),
        buffer (if (> (bill ware) (buffer ware))
                 (try
                   (swap! storage-atom #(- % (min (- (bill ware) (buffer ware)) amount)))
                   (update buffer ware #(+ % (min (- (bill ware) %) amount)))
                   (catch IllegalStateException _ buffer))
                 buffer),
        buffer (if (reduce-kv (fn [acc k v] (and acc (= v (bill k)))) true buffer)
                 (do (Thread/sleep (state :duration))
                     (send ((state :target-storage) :worker) supply-msg (state :amount))
                     (reduce-kv (fn [acc k _] (assoc acc k 0)) {} buffer))
                 buffer)]
    (assoc state :buffer buffer)
    )
  )

(def safe-storage (storage "Safe" 1))
(def safe-factory (factory 1 3000 safe-storage "Metal" 3))
(def cuckoo-clock-storage (storage "Cuckoo-clock" 1))
(def cuckoo-clock-factory (factory 1 2000 cuckoo-clock-storage "Lumber" 5 "Gears" 10))
(def gears-storage (storage "Gears" 20 cuckoo-clock-factory))
(def gears-factory (factory 4 1000 gears-storage "Ore" 4))
(def metal-storage (storage "Metal" 5 safe-factory))
(def metal-factory (factory 1 1000 metal-storage "Ore" 10))
(def lumber-storage (storage "Lumber" 20 cuckoo-clock-factory))
(def lumber-mill (source 5 4000 lumber-storage))
(def ore-storage (storage "Ore" 10 metal-factory gears-factory))
(def ore-mine (source 2 1000 ore-storage))

(defn start []
  (.start ore-mine)
  (.start lumber-mill))

(defn stop []
  (.stop ore-mine)
  (.stop lumber-mill))

(defn -main []
  (do
    (println "["(.format (new SimpleDateFormat "hh.mm.ss.SSS") (new Date))"] START TIME")
    (start)
    (Thread/sleep 100000)
    (stop)
    (println (agent-error (gears-factory :worker)))
    (shutdown-agents)
    ))