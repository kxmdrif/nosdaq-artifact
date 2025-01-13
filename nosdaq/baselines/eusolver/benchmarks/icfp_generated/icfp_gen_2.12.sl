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
(constraint (= (f #x4E958A368A3C3F4C) #x9D2B146D14787E99))
(constraint (= (f #x039ED3EFEF57D21C) #x073DA7DFDEAFA439))
(constraint (= (f #xD071131448A694A8) #xA0E22628914D2951))
(constraint (= (f #x1948B54CAE9B3CC2) #x32916A995D367985))
(constraint (= (f #xEE250159F4786134) #xDC4A02B3E8F0C269))
(constraint (= (f #x5601FD4445B9E76F) #x53FC0577748C3122))
(constraint (= (f #x6CE4A655A0A18029) #x2636B354BEBCFFAE))
(constraint (= (f #x64D77A449D3FFF59) #x36510B76C580014E))
(constraint (= (f #xBF0EDE50070375DD) #x81E2435FF1F91446))
(constraint (= (f #x2D67924C25842E9B) #xA530DB67B4F7A2CA))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0000000000000002))
(constraint (= (f #x346E81EE5E6E7B8E) #x68DD03DCBCDCF71D))
(constraint (= (f #x1B953974763CE562) #x372A72E8EC79CAC5))
(constraint (= (f #x97017B13600A38AA) #x2E02F626C0147155))
(constraint (= (f #x446BE8317C4E7E55) #x77282F9D07630356))
(constraint (= (f #x00275721E39063DE) #x004EAE43C720C7BD))
(constraint (= (f #x3D7264E6CE8182BD) #x851B363262FCFA86))
(constraint (= (f #x1BD8572C94021E76) #x37B0AE5928043CED))
(constraint (= (f #xE153274D4EEED5EA) #xC2A64E9A9DDDABD5))
(constraint (= (f #xE22EE857BB80E9C8) #xC45DD0AF7701D391))
(constraint (= (f #xA203CD9864E5A014) #x44079B30C9CB4029))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0000000000000002))
(check-synth)
