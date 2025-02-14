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
(constraint (= (f #x23D6E4D10AEE6BF0) #x47ADC9A215DCD7E2))
(constraint (= (f #x2605EF8CFC17D648) #x4C0BDF19F82FAC92))
(constraint (= (f #x6E27E30C730D9A8E) #xDC4FC618E61B351E))
(constraint (= (f #xD5200FC82516E2B4) #xAA401F904A2DC56A))
(constraint (= (f #x6258D286B46580B6) #xC4B1A50D68CB016E))
(constraint (= (f #x7892446F98B9E5E1) #xF12488DF3173CBC0))
(constraint (= (f #x54A8BC8FD38646DD) #xA951791FA70C8DB8))
(constraint (= (f #x8C515D54BF3B90B1) #x18A2BAA97E772160))
(constraint (= (f #x77EE8C9BC9F05B33) #xEFDD193793E0B664))
(constraint (= (f #x80AE6440770FE7A3) #x015CC880EE1FCF44))
(constraint (= (f #xAAAAAAAAAAAAAAAB) #x0000000000000000))
(constraint (= (f #x2A58AE54944916D1) #x54B15CA928922DA0))
(constraint (= (f #x1175C4475DB9FC8C) #x22EB888EBB73F91A))
(constraint (= (f #x1F767A23EBB8605A) #x3EECF447D770C0B6))
(constraint (= (f #x5A56A11E802638D1) #xB4AD423D004C71A0))
(constraint (= (f #xE00F747711B93444) #xC01EE8EE2372688A))
(constraint (= (f #x834B83DB6509CB22) #x069707B6CA139646))
(constraint (= (f #xD9005751D3FC5443) #xB200AEA3A7F8A884))
(constraint (= (f #x45EF274488C96737) #x8BDE4E891192CE6C))
(constraint (= (f #xE3F732FA9ADD18A8) #xC7EE65F535BA3152))
(constraint (= (f #x051E96EDA5BAF936) #x0A3D2DDB4B75F26E))
(constraint (= (f #xAAAAAAAAAAAAAAAB) #x0000000000000000))
(constraint (= (f #xA45015508A8A284B) #x48A02AA115145094))
(check-synth)
