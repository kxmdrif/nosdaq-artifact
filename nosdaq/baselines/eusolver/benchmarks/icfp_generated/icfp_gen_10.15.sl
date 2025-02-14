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
(constraint (= (f #x74573F0B5D188227) #x00003A2B9F85AE8C))
(constraint (= (f #xBF12562E1DEC633C) #x00005F892B170EF6))
(constraint (= (f #x7B2E89FF000A5460) #x00003D9744FF8005))
(constraint (= (f #x205B8F5359D07CA0) #x0000102DC7A9ACE8))
(constraint (= (f #x3FA5CFDE1504AB64) #x00001FD2E7EF0A82))
(constraint (= (f #x2764F69EF464D50B) #x000013B27B4F7A32))
(constraint (= (f #xEED2FDB1D4286956) #x000077697ED8EA14))
(constraint (= (f #x4A047DF1BDFCA893) #x000025023EF8DEFE))
(constraint (= (f #xD37EA4B54D2E0482) #x000069BF525AA697))
(constraint (= (f #x4555826405A4A585) #x000022AAC13202D2))
(constraint (= (f #xF1FA82D0579D8DE3) #xE3F505A0AF3B1BC4))
(constraint (= (f #x9034B4EE6C87FDB6) #x206969DCD90FFB6C))
(constraint (= (f #xEDA1E5982025C3EC) #xDB43CB30404B87D8))
(constraint (= (f #x002EF7C4170AFFFF) #x005DEF882E15FFFC))
(constraint (= (f #x3B6B8A829653A4EF) #x76D715052CA749DC))
(constraint (= (f #x8000000000000018) #x0000000000000000))
(constraint (= (f #x8000000000000014) #x0000000000000000))
(constraint (= (f #x8000000000000017) #x0000000000000000))
(constraint (= (f #x000000000000001C) #x0000000000000000))
(constraint (= (f #x8000000000000012) #x0000000000000000))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFE000000000000))
(constraint (= (f #x143B385CE3DDB0C9) #x00000A1D9C2E71EE))
(constraint (= (f #x5996E2F886F1B195) #x00002CCB717C4378))
(constraint (= (f #x73EF4D95047B858C) #x000039F7A6CA823D))
(constraint (= (f #x03975411607DA246) #x000001CBAA08B03E))
(constraint (= (f #x786781840B5D1459) #x00003C33C0C205AE))
(check-synth)
