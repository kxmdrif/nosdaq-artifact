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
(constraint (= (f #x3E99B21531FA0595) #x1F4CD90A98FD02CB))
(constraint (= (f #x632AE637B00F385E) #x3195731BD8079C2F))
(constraint (= (f #xA98D1383083C0ED7) #x54C689C1841E076C))
(constraint (= (f #x73AD0B3213C58441) #x39D6859909E2C221))
(constraint (= (f #xCC71DE1D60888E82) #x6638EF0EB0444741))
(constraint (= (f #x0000000000036FC9) #x000000000001B7E5))
(constraint (= (f #x000000000003CA97) #x000000000001E54C))
(constraint (= (f #x00000000000206D1) #x0000000000010369))
(constraint (= (f #x00000000000259D4) #x0000000000012CEA))
(constraint (= (f #x000000000003A497) #x000000000001D24C))
(constraint (= (f #xEB3E02FF5C64EB69) #xD67C05FEB8C9D6D0))
(constraint (= (f #x9F48401AF3CAA5BC) #x3E908035E7954B78))
(constraint (= (f #x7192A02ABBB1C165) #xE3254055776382C8))
(constraint (= (f #x525405FE80B10AEE) #xA4A80BFD016215DC))
(constraint (= (f #x43C159FC3BA19838) #x8782B3F877433070))
(constraint (= (f #x0000000000000001) #x0000000000000001))
(constraint (= (f #x00000000000245AD) #x0000000000000000))
(constraint (= (f #x00000000000265FE) #x0000000000000004))
(constraint (= (f #x0000000000020E2D) #x0000000000000000))
(constraint (= (f #x0000000000036F75) #x0000000000000000))
(constraint (= (f #x000000000003D1F2) #x0000000000000004))
(constraint (= (f #xF0F0F0F0F0F0F0F2) #x7878787878787879))
(constraint (= (f #xECB76EAEA83C78BF) #xD96EDD5D5078F100))
(constraint (= (f #x161BFA1242C48825) #x2C37F42485891048))
(constraint (= (f #xF48B9385E8BB0379) #xE917270BD17606F0))
(constraint (= (f #x12F2BF00F8D5957D) #x25E57E01F1AB2AF8))
(constraint (= (f #x8B0A1600E218F85A) #x45850B00710C7C2D))
(constraint (= (f #x0FCBFE2AB3CC5EBD) #x1F97FC556798BD78))
(constraint (= (f #xC143E09D30370497) #x60A1F04E981B824C))
(constraint (= (f #x721B07543E4E0A0A) #x390D83AA1F270505))
(constraint (= (f #x4773CE0602C1B38E) #x23B9E7030160D9C7))
(constraint (= (f #x606D18F013EF8B3E) #xC0DA31E027DF167C))
(constraint (= (f #x000000000003D0A0) #x0000000000000000))
(constraint (= (f #x000000000003FF23) #x0000000000000004))
(constraint (= (f #x000000000003A9A6) #x0000000000000004))
(constraint (= (f #x0000000000029112) #x0000000000014889))
(constraint (= (f #x000000000003588F) #x000000000001AC48))
(constraint (= (f #xF0F0F0F0F0F0F0F2) #x7878787878787879))
(constraint (= (f #x45FEACE7338C1DBA) #x8BFD59CE67183B74))
(constraint (= (f #xA1242448A12B047E) #x42484891425608FC))
(check-synth)
