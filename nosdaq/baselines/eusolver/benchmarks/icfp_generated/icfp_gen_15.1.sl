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
(constraint (= (f #x75A5D2BC237CA0F0) #x075A5D2BC237CA0E))
(constraint (= (f #xBE4902B95DE3B752) #x0BE4902B95DE3B74))
(constraint (= (f #x4223C39E0619329F) #x04223C39E0619328))
(constraint (= (f #xA76E95752CAD6675) #x0A76E95752CAD666))
(constraint (= (f #x342173C1A10E107E) #x0342173C1A10E106))
(constraint (= (f #x2F2AE210FACD0E63) #x2F2AE503A8EE1E0F))
(constraint (= (f #xE597AA3ED49172E3) #xE597B8984F35602C))
(constraint (= (f #x04D377EA5F5F5F2D) #x04D3783796DE0522))
(constraint (= (f #xDE01051012CA6408) #xDE0112F0231B6534))
(constraint (= (f #x1B6CFD84499B174D) #x1B6CFF3B19735BE6))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #x0FFFFFFFFFFFFFFE))
(constraint (= (f #xFFFFFFFF00000002) #x00000FFEFFFFF002))
(constraint (= (f #x0000000000000001) #x0000000000000001))
(constraint (= (f #x3F037F9ADC5AA5E5) #x3F03838B145453AA))
(constraint (= (f #x33A0079FACAFA8E0) #x33A00AD9AD29A3AA))
(constraint (= (f #x15D48F257DD06F65) #x15D49082C6C2C742))
(constraint (= (f #x3059461AA812E354) #x03059461AA812E34))
(constraint (= (f #x301FF33BC78EAA28) #x301FF63DC6C266A0))
(constraint (= (f #xFAC5E48E763E32A7) #xFAC5F43AD4871A0A))
(constraint (= (f #x49AD1A1573081C53) #x049AD1A1573081C4))
(constraint (= (f #x3FC9A1382AC9CA32) #x03FC9A1382AC9CA2))
(constraint (= (f #xA2A5FD1F3ACDEE64) #xA2A607499A9FE210))
(constraint (= (f #x236DCFB034DF4490) #x0236DCFB034DF448))
(constraint (= (f #xFFFFFFFF00000002) #x00000FFEFFFFF002))
(check-synth)
