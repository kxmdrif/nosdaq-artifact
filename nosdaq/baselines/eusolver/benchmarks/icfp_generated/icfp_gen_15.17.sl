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
(constraint (= (f #xDC0DB1FE65D480D2) #x0B81B63FCCBA901A))
(constraint (= (f #xE0F434BA9EAC7FB0) #x0C1E869753D58FF6))
(constraint (= (f #xF43741A592853010) #x0E86E834B250A602))
(constraint (= (f #x95135627F5825940) #x02A26AC4FEB04B28))
(constraint (= (f #x59155AD2C35D2CC2) #x0B22AB5A586BA598))
(constraint (= (f #x92CF046CCF765861) #x0259E08D99EECB0C))
(constraint (= (f #x3B96819DDE473553) #x0772D033BBC8E6AA))
(constraint (= (f #x81164B48F6E2BF91) #x0022C9691EDC57F2))
(constraint (= (f #x511B861337D85403) #x0A2370C266FB0A80))
(constraint (= (f #xDFB1DB0BDDFF1E21) #x0BF63B617BBFE3C4))
(constraint (= (f #xC7312AF5229EDA76) #x0000000000000000))
(constraint (= (f #x8D9A6B958141DB54) #x0000000000000000))
(constraint (= (f #x0DBD8C5EB9B503F4) #x0000000000000000))
(constraint (= (f #xFFCAADC3278EB616) #x0000000000000000))
(constraint (= (f #x17A796212C115A16) #x0000000000000000))
(constraint (= (f #xCAEE047FCC55E23A) #x095DC08FF98ABC47))
(constraint (= (f #x319353B27B28AC38) #x06326A764F651587))
(constraint (= (f #xE60046EB0C554E6A) #x0CC008DD618AA9CD))
(constraint (= (f #xFA0A6E3668C673E8) #x0F414DC6CD18CE7D))
(constraint (= (f #x3C0DA109236C7D18) #x0781B421246D8FA3))
(constraint (= (f #xFFFF000000000002) #x0000000000000000))
(constraint (= (f #x3F6976EBDF756C55) #x0812D12284115275))
(constraint (= (f #x429D92B23E6C6DE5) #x07AC4DA9B8327243))
(constraint (= (f #xC204B26934BB89FF) #x07BF69B2D9688EC0))
(constraint (= (f #xE53B39ECD9AA31BF) #x035898C264CAB9C8))
(constraint (= (f #xB71E26328DC36B85) #x091C3B39AE47928F))
(constraint (= (f #x03C3060B4850C213) #x007860C1690A1842))
(constraint (= (f #xF01A5094870D0291) #x0E034A1290E1A052))
(constraint (= (f #x6812161C0C02D0D3) #x0D0242C381805A1A))
(constraint (= (f #x40124104925A0C11) #x08024820924B4182))
(constraint (= (f #x2960A042584180B1) #x052C14084B083016))
(constraint (= (f #xBCE0B342E395C8DB) #x079C16685C72B91B))
(constraint (= (f #x97E4AC0082231FF9) #x02FC9580104463FF))
(constraint (= (f #xF3B0CB2A1D16873B) #x0E76196543A2D0E7))
(constraint (= (f #x142B585578B926D9) #x02856B0AAF1724DB))
(constraint (= (f #xD0BC7DD31AF848EB) #x0A178FBA635F091D))
(constraint (= (f #x6A4153AC5C21C47C) #x0000000000000000))
(constraint (= (f #xEB4102EA42A31DDC) #x0000000000000000))
(constraint (= (f #xFFB3FBF655E6169C) #x0000000000000000))
(constraint (= (f #x0F0CC63F3D08462C) #x0000000000000000))
(constraint (= (f #x230AA4CE941522AE) #x0000000000000000))
(constraint (= (f #x0F06820E1C203435) #x01E0D041C3840686))
(constraint (= (f #x2041A412D0C29695) #x040834825A1852D2))
(constraint (= (f #x34168680E1618095) #x0682D0D01C2C3012))
(constraint (= (f #x90C296043480521F) #x021852C086900A43))
(constraint (= (f #x78000680A503481F) #x0F0000D014A06903))
(constraint (= (f #xEFA20A70EC9C40FD) #x020BBEB1E26C77E0))
(constraint (= (f #x39006EFB9AB9C56D) #x08DFF2208CA8C752))
(constraint (= (f #x770523128E07BE4D) #x011F5B9DAE3F0836))
(constraint (= (f #x92BBBB3484567FB7) #x0DA888996F753009))
(constraint (= (f #x6D240D6525072B37) #x025B7E535B5F1A99))
(constraint (= (f #xE041E07807861859) #x03F7C3F0FF0F3CF4))
(constraint (= (f #x7824A5A0524B0839) #x00FB6B4BF5B69EF8))
(constraint (= (f #x28290F010D25025B) #x0AFADE1FDE5B5FB4))
(constraint (= (f #xE1804A1296090C39) #x03CFF6BDAD3EDE78))
(constraint (= (f #x212903C341061E1B) #x0BDADF8797DF3C3C))
(constraint (= (f #xC124A1294878061D) #x07DB6BDAD6F0FF3C))
(constraint (= (f #xC08581C034202C17) #x07EF4FC7F97BFA7D))
(constraint (= (f #xD0A40A0801C02017) #x05EB7EBEFFC7FBFD))
(constraint (= (f #xE0D20A02D2581A1D) #x03E5BEBFA5B4FCBC))
(constraint (= (f #x4024A14294A05A1D) #x07FB6BD7AD6BF4BC))
(check-synth)
