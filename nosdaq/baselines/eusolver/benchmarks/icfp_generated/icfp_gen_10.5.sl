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
(constraint (= (f #xF4B1FC5253A55E80) #xE963F8A4A74ABD00))
(constraint (= (f #x0171AF97F3A5BA44) #x02E35F2FE74B7488))
(constraint (= (f #xFB437CCA01E0B326) #xF686F99403C1664C))
(constraint (= (f #x256F00216065D9B8) #x4ADE0042C0CBB370))
(constraint (= (f #x215AA5574F68972C) #x42B54AAE9ED12E58))
(constraint (= (f #x800094D727CB8D70) #x000129AE4F971AE0))
(constraint (= (f #x8000CBC73DCAD9FA) #x0001978E7B95B3F4))
(constraint (= (f #x000094145FA4EF42) #x00012828BF49DE84))
(constraint (= (f #x0000868DA381E3A0) #x00010D1B4703C740))
(constraint (= (f #x00009320D80F5D90) #x00012641B01EBB20))
(constraint (= (f #xA2CA46045B6DEACB) #x45948C08B6DBD597))
(constraint (= (f #xE967D3D98E5A023D) #xD2CFA7B31CB4047B))
(constraint (= (f #xB725E2BFFB168125) #x6E4BC57FF62D024B))
(constraint (= (f #x7049D0F5E61CA495) #xE093A1EBCC39492B))
(constraint (= (f #x718773A12A580DA7) #xE30EE74254B01B4F))
(constraint (= (f #x484884A22A125253) #x909109445424A4A7))
(constraint (= (f #xA52A908920A48813) #x4A55211241491027))
(constraint (= (f #xA155120915549523) #x42AA24122AA92A47))
(constraint (= (f #x290AAAA88012A953) #x52155551002552A7))
(constraint (= (f #x802488288942A24B) #x0049105112854497))
(constraint (= (f #x0000E621FBDB5BE1) #x0000000000000001))
(constraint (= (f #x8000A1741370E223) #x0000000000000001))
(constraint (= (f #x000085C7FBB89C4D) #x0000000000000001))
(constraint (= (f #x0000E340744C41A5) #x0000000000000001))
(constraint (= (f #x80009C3167FF6D2F) #x0000000000000001))
(constraint (= (f #x8000A922A2152903) #x0000000000000001))
(constraint (= (f #x00009544955542A3) #x0000000000000001))
(constraint (= (f #x8000A924A4A252A3) #x0000000000000001))
(constraint (= (f #x0000A448A84848A3) #x0000000000000001))
(constraint (= (f #x0000925052A54A4B) #x0000000000000001))
(constraint (= (f #xD35E8870D6C6ACFF) #xA6BD10E1AD8D59FF))
(constraint (= (f #x0FAF48C485694C86) #x1F5E91890AD2990C))
(constraint (= (f #x2F5A828989BE50A3) #x5EB50513137CA147))
(constraint (= (f #xAA846862B6F27589) #x5508D0C56DE4EB13))
(constraint (= (f #x5DAAD0D4D4D07492) #xBB55A1A9A9A0E924))
(constraint (= (f #xFD07C23534A55FDE) #xFA0F846A694ABFBC))
(constraint (= (f #xB377732C8D7518A5) #x66EEE6591AEA314B))
(constraint (= (f #xDCF86EB527F31C8C) #xB9F0DD6A4FE63918))
(constraint (= (f #xB889612F5BBBFE31) #x7112C25EB777FC63))
(constraint (= (f #x58C4CBA925B0143D) #xB18997524B60287B))
(constraint (= (f #x8000C8F0029E946F) #x0000000000000001))
(constraint (= (f #x0000955248512903) #x0000000000000001))
(constraint (= (f #xA4A4A854954502A3) #x494950A92A8A0547))
(constraint (= (f #x0000DB42691F1B16) #x0001B684D23E362C))
(check-synth)
