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
(constraint (= (f #x19C6604701A23285) #x338CC08E0344650C))
(constraint (= (f #x57C5E08C2BB09853) #xAF8BC118576130A8))
(constraint (= (f #x4F933A7937C2C3AF) #x9F2674F26F858760))
(constraint (= (f #x85DDA4BDECE2633D) #x0BBB497BD9C4C67C))
(constraint (= (f #xB2E3A1BC94D61597) #x65C7437929AC2B30))
(constraint (= (f #xA0F7B861E9C68A12) #x41EF70C3D38D1426))
(constraint (= (f #xCFB61D64FF207A0A) #x9F6C3AC9FE40F416))
(constraint (= (f #x086D6E23F3C854B4) #x10DADC47E790A96A))
(constraint (= (f #xA8C2347326B0D17E) #x518468E64D61A2FE))
(constraint (= (f #x17477B608070479C) #x2E8EF6C100E08F3A))
(constraint (= (f #x353B7F2032B35A9D) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x19F58F95C12D14A3) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x5BF7B2FD69376C1F) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x64E8BBE74AC128CF) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x339B720253D33E01) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x8A1A92828187B186) #x14352505030F630E))
(constraint (= (f #x8EBFEB9D57E3DF42) #x1D7FD73AAFC7BE86))
(constraint (= (f #x38C3698CE65959A6) #x7186D319CCB2B34E))
(constraint (= (f #x8565F36990E3791C) #x0ACBE6D321C6F23A))
(constraint (= (f #xE5536BE29D59FE30) #xCAA6D7C53AB3FC62))
(constraint (= (f #x41D6B5179D72FC3A) #x83AD6A2F3AE5F876))
(constraint (= (f #x51A3FEE536F5A929) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x30CAC3B5D6A58C2D) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x43F5FA6DD0F8C194) #x87EBF4DBA1F1832A))
(constraint (= (f #x1B0FDCAE756D9AA3) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x6244CF93D2AB12A0) #xC4899F27A5562542))
(constraint (= (f #x9F38EACCBCEB9F6D) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x151BCBD42286201C) #x2A3797A8450C403A))
(constraint (= (f #xE740FBEC82BC0449) #xCE81F7D905780894))
(constraint (= (f #x67D043EBD677B93D) #xFFFFFFFFFFFFFFFF))
(check-synth)
