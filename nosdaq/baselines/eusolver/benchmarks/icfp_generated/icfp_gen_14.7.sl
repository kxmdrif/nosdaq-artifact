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
(constraint (= (f #x7A51B30C58FE0230) #x8EF2D514E9020650))
(constraint (= (f #x5271B1221413D2D1) #xF692D3663C347772))
(constraint (= (f #x6238CBAD0F5BC767) #xA6495CF711EC49A8))
(constraint (= (f #xE7626A0A918E2D69) #x29A6BE1FB29277BA))
(constraint (= (f #xE5138119F353379B) #x2F34832A15F558AC))
(constraint (= (f #xB093012C38934429) #xD1B5037449B5CC7A))
(constraint (= (f #x8E804138980366DD) #x9380C349A805AB66))
(constraint (= (f #x1A7A4004348B4235) #x2E8EC00C5D9DC65E))
(constraint (= (f #xDF900004C1B3184D) #x60B0000D42D528D6))
(constraint (= (f #x604718380243AC29) #xA0C9284806C4F47A))
(constraint (= (f #x68D8C3C211351335) #xF97273C3DEECAECC))
(constraint (= (f #xA07D554F63A5BCAC) #xF5F82AAB09C5A435))
(constraint (= (f #x53126934AF652A57) #xFACED96CB509AD5A))
(constraint (= (f #x25E7628B2255BBD7) #xFDA189D74DDAA442))
(constraint (= (f #xF3242C8D2461545B) #xF0CDBD372DB9EABA))
(constraint (= (f #xFFFFC0000FFFFC02) #xF00003FFFF00003F))
(constraint (= (f #x2B3494C04305BC91) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x3240C1220E995047) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x2AD151060629C091) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0D03F0CC0A010563) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #xD705207A09854409) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFF))
(check-synth)
