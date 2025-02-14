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
(constraint (= (f #x7B876DD1AFE65456) #xC23C4917280CD5D4))
(constraint (= (f #xBEC03F359B29EDCB) #xA09FE065326B091A))
(constraint (= (f #x27261235DB5F4B43) #xEC6CF6E512505A5E))
(constraint (= (f #xEA2A045DAE9752D8) #x8AEAFDD128B45693))
(constraint (= (f #x5D75032EECE39B8A) #xD1457E68898E323A))
(constraint (= (f #x8000000000000000) #xBFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000000) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #xAA87754D3E75BBEB) #xFF1FFFBEFDFFFFFE))
(constraint (= (f #x5EE084D5CE03D0F1) #xFFC31BFFBC0FE3E6))
(constraint (= (f #x87956B56289AC8FC) #x1F7FFFFCF37FB3F8))
(constraint (= (f #xDBD9C2E04C0FE37B) #xFFF78FC1B83FCFFE))
(constraint (= (f #xB84281C031AE2F63) #xF18F0780E7FCFFCE))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x8000000000000000))
(constraint (= (f #xF0F0F0F0F0F0F0F2) #xE3E3E3E3E3E3E3EC))
(constraint (= (f #xE8E00AFF4F2F1D5A) #x8B8FFA8058687152))
(constraint (= (f #xBC6C33B8EE97DD5F) #xA1C9E62388B41150))
(constraint (= (f #xA87D36C5D96BC8CF) #xABC1649D134A1B98))
(constraint (= (f #x24677E8CFE551CF9) #xD9DFFF3BFDFE7BF6))
(constraint (= (f #x45CF3AA017840F30) #x9FBEFFC07F183EE0))
(constraint (= (f #x9457B0C5B8D75442) #xB5D4279D239455DE))
(constraint (= (f #xAD22D1525B545064) #xFECFE7EDFFF9E1D8))
(constraint (= (f #x49CE5EDDA88A4B13) #xDB18D0912BBADA76))
(constraint (= (f #x36423CFB2BF7D674) #xFD8CFBFEFFFFFDF8))
(constraint (= (f #x91FEBF2C2BE475A8) #x67FFFEF8FFD9FFF0))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x8000000000000000))
(constraint (= (f #x7FFFFFFFFFFFFFFF) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFFFFFFFFFFFFC))
(constraint (= (f #x07454DBBCE2295B3) #x1F9FBFFFBCCF7FEE))
(constraint (= (f #xF0F0F0F0F0F0F0F2) #xE3E3E3E3E3E3E3EC))
(check-synth)
