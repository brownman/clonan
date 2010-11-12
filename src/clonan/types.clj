;;  This file is part of clojure-warrior.
;;
;;  clojure-warrior is free software: you can redistribute it and/or modify
;;  it under the terms of the GNU General Public License as published by
;;  the Free Software Foundation, either version 3 of the License, or
;;  (at your option) any later version.
;;
;;  clojure-warrior is distributed in the hope that it will be useful,
;;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;  GNU General Public License for more details.
;;
;;  You should have received a copy of the GNU General Public License
;;  along with clojure-warrior.  If not, see <http://www.gnu.org/licenses/>.

(in-ns 'clonan.core)

(deftype player []
  IPlayer
  (play-turn [_ clonan] (walk! clonan)))

(deftype wall []
  IThing
  (to-char [_] \-))

(defrecord warrior [last-action]
  IThing
  (to-char [_] \@)

  ILevelOneMethods
  (walk! [_] (reset! last-action :walk!)))

(deftype sludge []
  IThing
  (to-char [_] \s))

(deftype stairs []
  IThing
  (to-char [_] \>))

(deftype vacancy []
  IThing
  (to-char [_] \space))

(deftype board [width rows]
  IBoard
  
  (print [_]
    (let [wall (border-row-to-string width)]
      (println wall)
      (doseq [row rows] (println (row-to-string row)))
      (println wall)))

  (find [self expected-type]
    (first (positions #(= expected-type (class %)) (row self))))

  (get-warrior [self]
    (nth (row self) (find self warrior)))

  (get-stairs [self]
    (nth (row self) (find self stairs)))

  (transform [self event]
    (let [clonan (get-warrior self)
          clonan-pos (find self warrior)]
    (if (= event :walk!)
      (respond-to-walk! self)
      (throw (Exception. (str "unexpected action of " event))))))
  
  ;; warts
  (content-width [_] width)
  (row [_] (first rows))

  (respond-to-walk! [self]
     (let [clonan-pos (find self warrior)
           clonan (get-warrior self)
           new-row (assoc (row self) clonan-pos (vacancy.) (inc clonan-pos) clonan)]
       (println "warrior walks forward")
       (board. width (vector new-row)))))
