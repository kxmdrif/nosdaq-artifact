(set-logic BV)

(define-fun shr1 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000001))
(define-fun shr4 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000004))
(define-fun shr16 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000010))
(define-fun shl1 ((x (BitVec 64))) (BitVec 64) (bvshl x #x0000000000000001))
(define-fun if0 ((x (BitVec 64)) (y (BitVec 64)) (z (BitVec 64))) (BitVec 64) (ite (= x #x0000000000000001) y z))

(synth-fun f ( (x (BitVec 64))) (BitVec 64)
(

(Start (BitVec 64) (#x0000000000000000 #x0000000000000001 x (bvnot Start)
                    (shl1 Start)
 		    (shr1 Start)
		    (shr4 Start)
		    (shr16 Start)
		    (bvand Start Start)
		    (bvor Start Start)
		    (bvxor Start Start)
		    (bvadd Start Start)
		    (if0 Start Start Start)
 ))
)
)
(constraint (= (f #xD62180AE64FD2654) #x0000000000000000))
(constraint (= (f #x2D35A0592B916208) #x0000000000000000))
(constraint (= (f #x590C1B476AC114B4) #x0000000000000000))
(constraint (= (f #xD053084940F99758) #x0000000000000000))
(constraint (= (f #x8D10A6557961D750) #x0000000000000000))
(constraint (= (f #x25CEB5D256071341) #x0000000000000000))
(constraint (= (f #x72BDE272E713B821) #x0000000000000000))
(constraint (= (f #xF6C16177B2330FDB) #x0000000000000000))
(constraint (= (f #x20FE7339AFB1891F) #x0000000000000000))
(constraint (= (f #x1D6825C11B07DA3B) #x0000000000000000))
(constraint (= (f #x9B4683B1FA7805A2) #x0000000000000000))
(constraint (= (f #xC99E9749106CCB76) #x0000000000000000))
(constraint (= (f #xF8DA615C67CA17CA) #x0000000000000000))
(constraint (= (f #x069536D77EACA23C) #x0000000000000000))
(constraint (= (f #x6B850F9D64AEABC0) #x0000000000000000))
(constraint (= (f #x91C0303E1A085045) #x0000000000000000))
(constraint (= (f #x259A8C6046081B79) #x0000000000000000))
(constraint (= (f #xDF4EC62142322019) #x0000000000000000))
(constraint (= (f #x644DDF1321C4B281) #x0000000000000000))
(constraint (= (f #x1DB8DE54469CAB53) #x0000000000000000))
(constraint (= (f #xC80E0050DB814622) #x0000000000000000))
(constraint (= (f #x818EE25C17D12C2C) #x0000000000000000))
(constraint (= (f #x27E39D3040C71C7C) #x0000000000000000))
(constraint (= (f #x4EF907D52C142921) #x0000000000000000))
(constraint (= (f #xFAF8DF378A6CE593) #x0000000000000000))
(constraint (= (f #xCFA915F6549478C4) #x0000000000000000))
(constraint (= (f #x664681B6AA09815E) #x0000000000000000))
(constraint (= (f #x0B45BD9FB86314F6) #x0000000000000000))
(constraint (= (f #x02148DAB5C584D1F) #x0000000000000000))
(constraint (= (f #x8E6FFB86582EB48A) #x0000000000000000))
(constraint (= (f #x5C9E14D30AE97FB5) #x0000000000000000))
(check-synth)
