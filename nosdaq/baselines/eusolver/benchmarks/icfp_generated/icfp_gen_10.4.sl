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
(constraint (= (f #x82551C1669785F7C) #x086FF243ABB88E18))
(constraint (= (f #x0853E31FF0D3ACEA) #x018F425201174F53))
(constraint (= (f #x715373A4AAE44A94) #x093F594EDFF2CDFB))
(constraint (= (f #x7BF558B5019818AA) #x08C1FE9DF02A829F))
(constraint (= (f #xDDEF7400BC1EABC0) #x066319C01C423FC4))
(constraint (= (f #x0E07E99D49FA7CBB) #xFFFFF1F81662B605))
(constraint (= (f #x942D0C2B048C51B5) #xFFFF6BD2F3D4FB73))
(constraint (= (f #x1DA1FADAA56869CF) #xFFFFE25E05255A97))
(constraint (= (f #x0700FEBDF281F5F5) #xFFFFF8FF01420D7E))
(constraint (= (f #x0409D1F4546ED5A1) #xFFFFFBF62E0BAB91))
(constraint (= (f #x000000000000D1E2) #x0000000000002A44))
(constraint (= (f #x000000000000721C) #x0000000000001244))
(constraint (= (f #x0000000000005978) #x0000000000001551))
(constraint (= (f #x000000000000C2FC) #x00000000000028A0))
(constraint (= (f #x0000000000002BC8) #x0000000000000A89))
(constraint (= (f #x00000000000040DF) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x000000000000AB37) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000006237) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000000029D3) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000004EA5) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x17E0E7F6881FD496) #x03821281B98207DB))
(constraint (= (f #x7989EE81DF6E7F8F) #xFFFF8676117E2091))
(constraint (= (f #xD98C0D2BAE75A987) #xFFFF2673F2D4518A))
(constraint (= (f #xEB31649FB0739596) #x03D53ADA0D094BEB))
(constraint (= (f #x1D1A02426097D957) #xFFFFE2E5FDBD9F68))
(constraint (= (f #x96D76DB55AFB2487) #xFFFF6928924AA504))
(constraint (= (f #x7514E8BC0AEC7553) #xFFFF8AEB1743F513))
(constraint (= (f #xB4E7A1AA7689ABBC) #x0DD28E2FE9B9AFCC))
(constraint (= (f #xC1D91DE277EFC9F1) #xFFFF3E26E21D8810))
(constraint (= (f #xBA37BDB576E14DA8) #x0CE58C6DF9B23D6F))
(constraint (= (f #x000000000000FE0A) #x0000000000002041))
(constraint (= (f #x0000000000000241) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x000000000000F8C3) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000004122) #x0000000000000824))
(constraint (= (f #x4000000000006AD4) #x0C00000000000BF7))
(constraint (= (f #x40000000000069E4) #x0C00000000000BA2))
(constraint (= (f #x0000000000015606) #x0000000000003FA0))
(constraint (= (f #xC000000000007AAA) #x04000000000008FF))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFF))
(check-synth)
