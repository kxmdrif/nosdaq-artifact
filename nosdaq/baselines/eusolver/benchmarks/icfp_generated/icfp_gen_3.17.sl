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
(constraint (= (f #xF5523E7117EC881B) #xEAA47CE22FD91036))
(constraint (= (f #xEEB86663F5C69807) #xDD70CCC7EB8D300E))
(constraint (= (f #x80C8053990685079) #x01900A7320D0A0F2))
(constraint (= (f #x7B41B309DD604932) #xF6836613BAC09264))
(constraint (= (f #x889965BC5162EFD3) #x1132CB78A2C5DFA6))
(constraint (= (f #x8B55EEBE9E2065A1) #x16ABDD7D3C40CB42))
(constraint (= (f #x8B9A1F685E0037A9) #x17343ED0BC006F52))
(constraint (= (f #x12223DF7DC087775) #x24447BEFB810EEEA))
(constraint (= (f #xA15B02DA2502D2B0) #x42B605B44A05A560))
(constraint (= (f #x506DA7874E0AEF2C) #xA0DB4F0E9C15DE58))
(constraint (= (f #xBB27D9230BFC55C7) #x764FB24617F8AB8E))
(constraint (= (f #x004C877C5DF8C166) #x00990EF8BBF182CC))
(constraint (= (f #x56EB7EEAF7D0F9ED) #xADD6FDD5EFA1F3DA))
(constraint (= (f #xFCFB0F0412D81116) #xF9F61E0825B0222C))
(constraint (= (f #x5687B45B617A10EF) #xAD0F68B6C2F421DE))
(constraint (= (f #xD74662AE82CF1C05) #xD74662AE82CF1C05))
(constraint (= (f #x1D6D2BCBF379AAAE) #x1D6D2BCBF379AAAE))
(constraint (= (f #x67CCF49AF4E13152) #x67CCF49AF4E13152))
(constraint (= (f #x70D02813464F8D19) #x70D02813464F8D19))
(constraint (= (f #x8C7DC3AD5ADB7E23) #x8C7DC3AD5ADB7E23))
(constraint (= (f #xFD43EEC95792FD98) #xFA87DD92AF25FB30))
(constraint (= (f #x250C9DCD6C185CE5) #x4A193B9AD830B9CA))
(constraint (= (f #x5AF89537D4BCA804) #xB5F12A6FA9795008))
(constraint (= (f #x341623AC8190607E) #x682C47590320C0FC))
(constraint (= (f #x719C110DAB3E66E1) #xE338221B567CCDC2))
(constraint (= (f #x0000000000000001) #x0000000000000002))
(constraint (= (f #xF0F0F0F0F0F0F0F2) #xF0F0F0F0F0F0F0F2))
(constraint (= (f #x87FDFB7C07B9E3AD) #x0000000000000001))
(constraint (= (f #x2A83DF0A489F6863) #x0000000000000001))
(constraint (= (f #x8DB386CC7815CF28) #x0000000000000001))
(constraint (= (f #x125B07FC949B9922) #x0000000000000001))
(constraint (= (f #xFB1D43CF81939C8C) #x0000000000000001))
(check-synth)
