package com_ravnaandtines;

/** 
 *  Class InnerWorld 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 dd-mmm-yyyy
 *
 */
import java.awt.image.*;
import java.util.*;
import net.sf.functionalj.*;
import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.util.ListUtils;

class InnerWorld implements ImageProducer { //NOPMD -- "too many methods" -- fine grained methods

    public static final int WIDTH = 194;
    public static final int HEIGHT = 168;
    private static final int[] col = {0xff0082ff, 0xff000000, 0xffffffff, 0xff008200};
    private static final long[] image = {
        0x540bf93411741e7L, 0x620540bf93411742L, 0xe740200541bf9241L,
        0x7470841e6412006L, 0x41bf90410840c543L, 0x640e640210741bfL,
        0x894080440841c840L, 0x146e441210842bfL, 0x83490940c9400045L,
        0x40e040c1412209L, 0x42bf8044c4410941L, 0xc849df45220b41bdL,
        0x43c5420a41c941c0L, 0x43dd440140240c41L, 0xbb42c6420b40ca41L,
        0xc041de44280d41baL, 0x41c7410b41e9452cL, 0xe42b842c3430c40L,
        0xe9462c0f42b742c2L, 0x430c41e8462d1043L, 0xb54000440b44e943L,
        0x300f45b44001400eL, 0x43e944300f420042L, 0xb2410f42eb453014L,
        0x42b1410f41eb4630L, 0x1642af410f41eb45L, 0x311743ad44014007L,
        0x44eb45301844ab4aL, 0x443ec45301a42aaL, 0x41c242c0420242eeL,
        0x44321c42a841c642L, 0x241f042331d41a7L, 0x42c6410241f14134L,
        0x1e42a541c7410240L, 0xf141351f43a241c8L, 0x400341e140cd4017L,
        0x411c1f449f42c942L, 0x141e042ca421544L, 0x1b204001429b43caL,
        0x420041e043c94314L, 0x451b25419943cc43L, 0xe143c94214451c26L,
        0x439542ce400041e0L, 0x43c94314441d2844L, 0x8e408042d042e044L,
        0xc94017431e294680L, 0x4dc841c742df45c3L, 0x40c341144200411fL,
        0x2843c047c341cb44L, 0xc641de46c7411543L, 0x212844d8410141c6L,
        0x42dd45c741154421L, 0x2a42d844c841dd45L, 0xc64104400e45222aL,
        0x42d941c040e845c4L, 0x421445232a42ffc6L, 0x43c4441444242b41L,
        0xc041f342cc43c443L, 0xc04210400042252cL, 0x43f341cd43c44000L,
        0x41c0410f45262c43L, 0xf242cd43c4400044L, 0xf44272d42c341d0L,
        0x4ac140cd42cc44c4L, 0x470d44282e40c342L, 0xcd66c341c143c645L,
        0xf43282d41c242ccL, 0x45c140c043c043c0L, 0x45c04ac041c242c1L,
        0x43c5470e412a2d40L, 0xc342cb43c041c045L, 0xc143c042c044c042L,
        0xc042c141c242c242L, 0xc444c1430d402640L, 0x22d41c0420040c9L,
        0x43c241c041c045c0L, 0x41c048c042c046c0L, 0x45c043c141c044c1L,
        0x433541012e420042L, 0xc843c44ac063c048L, 0xc1423642003242c6L,
        0x43ca41e543c048c1L, 0x4236420019411541L, 0xc743cb43cc45d043L,
        0xc048c14334420117L, 0x431541c742cd46c5L, 0x48d142c049c04135L,
        0x430117431543c542L, 0xcf46c342d742c143L, 0x472e4000420041L,
        0x317421842c640d1L, 0x45dd42c248c0412cL, 0x4705174207420e41L,
        0xc142d546db43c24cL, 0x1241134509174306L, 0x420e46d447db44c2L,
        0x4b114211450b1743L, 0x6411341d24bd946L, 0xc247134310450c17L,
        0x431c40d245c342c2L, 0x41d447c147114000L, 0x43004405490d1744L,
        0x1a41c841c543ca40L, 0xc141d546c1461245L, 0x14ac0470d17431bL,
        0x41c844c242c947ccL, 0x47c242c243c04213L, 0x4201530e16451b41L,
        0xc843c241ca49c141L, 0xc4420343c142c243L, 0xc0421345c0410542L,
        0x450e17421d41dcL, 0x530642c142c243c0L, 0x420b450041c142c1L,
        0x4102401917421d41L, 0xd450004200420a41L, 0xc240c244c1430952L,
        0x42004100480b17L, 0x411d41d150004212L, 0x42c241c043c2420bL,
        0x45c14c0142c041c0L, 0x450a17401e43cf42L, 0xc14605411340004bL,
        0xc1410c45c145c045L, 0x43c041c0450a34L, 0x47cf41c143204ac2L,
        0x400e41c042c04c00L, 0x43c041c0430c3242L, 0xc045c740c040c040L,
        0xc2462141c04c0b43L, 0xc159c0430a2f4ec3L, 0x47c0452341c04c0bL,
        0x42c044c144c043c0L, 0x42c04a084124410aL, 0x4dc1420246004101L,
        0x401e510c43c144c0L, 0x43c046c14d024323L, 0x420243034dc04304L,
        0x4304411e44014200L, 0x4100420e42c04101L, 0x430041c341c042c1L,
        0x46c0430241c04023L, 0x4203420542c04c05L, 0x412641c040024500L,
        0x420f4501410143c2L, 0x4e01400143c04024L, 0x410c430143004300L,
        0x402f422040034001L, 0x44c440c1410a40c0L, 0x4435412042194022L,
        0x42014000420043c0L, 0x450041004d35411fL, 0x433e4103410141c0L,
        0x4700410147c2413fL, 0x18423f0841c144c0L, 0x400241c0440041c1L,
        0x413f3f24400040c0L, 0x4d004003443f3f18L, 0x410140084cc04005L,
        0x42c0413f3f1c4106L, 0x4bc044004100453fL, 0x3f19440746c041c1L,
        0x4701443f3f1c4200L, 0x400440c044c04c01L, 0x433f3f1840024400L,
        0x400046c04d014100L, 0x410641004d00463fL, 0x3a40004dc04e0606L,
        0x583f3940c042c049L, 0xc14dc04104055000L, 0x473f3a40004bc140L,
        0xc044c042c042c242L, 0x1055000473f3341L, 0x400747c244c045L,
        0xc240c041c040013fL, 0x658004e23410040L, 0x420142c045c043L,
        0xc041c043c041c046L, 0x13f064b00490040L, 0x40004100492941L,
        0x1410040c046c341L, 0xc049c043013f0559L, 0x4d2e400140c145L,
        0xc241c24c023f0545L, 0x51014100480040L, 0x3040c05c023f3f1cL,
        0x410040c05b014000L, 0x3f3f1c600041003fL, 0x3f1d400340c047c0L,
        0x40c046c146013f3fL, 0x1b400654c241033fL, 0x3f20400143c041c0L,
        0x47c04a0124423f37L, 0x410440c048c042c1L, 0x40c0430324453f25L,
        0x44014001480048c1L, 0x44c0470325442240L, 0x410a4002420341L,
        0x2641004100410148L, 0x48c14e02204102L, 0x4322520048114013L,
        0x44004200480041c0L, 0x45c14ac042022041L, 0x143234f00410047L,
        0x1043124300430043L, 0x410241c045c143L, 0xc046c240011f4300L,
        0x4224540045104411L, 0x41004a00410241c0L, 0x45c04bc142011e47L,
        0x2b4124460f51005bL, 0x11d42014205402bL, 0x431945c0432345c0L,
        0x42c04bc040021c47L, 0x54329431940c045L, 0xc0421b410442c045L,
        0xc04ac1431b470345L, 0x2a431941c043c140L, 0xc0401b41024bc044L,
        0xc044c241c01a4200L, 0x4303442c431645c0L, 0x41c3431f4bc047c7L,
        0x1944004104450041L, 0x3f0249c4431d53c5L, 0x41c1184705450141L,
        0x413d48c041c342L, 0x2044c049c7431742L, 0x142044b00453a4aL,
        0xc3421f44c046ca44L, 0x164500400448c148L, 0x44031410149c144L,
        0x1f4bc042c4471546L, 0x648c142c1430242L, 0x30420143c042c342L,
        0x2046c043c142c447L, 0x144000450549c041L, 0xc242c14430440045L,
        0xc443204cc040c943L, 0xc0134101430655c2L, 0x402f42c047c5421fL,
        0x47c045c043c840c1L, 0x124301410458c141L, 0x2e480042c041c142L,
        0x204ec042c040c740L, 0xc111470454c041c0L, 0x41c0402d49034822L,
        0x400042c041c040c0L, 0x49c810430042035cL, 0xc0402c4906462141L,
        0x42c041c04cc041L, 0xc3400f470259c043L, 0xc0402b44c0420940L,
        0x25400344c341c046L, 0xc042c1410e47035dL, 0xc1402b44c1403545L,
        0xc244c04b000d4704L, 0x430355c1402c4000L, 0x4333400146c041c0L,
        0x440047020e460c4bL, 0xc146c1412f413841L, 0xc043c041c0430141L,
        0x41040f440d52c1L, 0x4230403646c045c1L, 0x4201400140041042L,
        0xe4ac042c042c141L, 0x3f2f44c041c14102L, 0x40070f42114bc141L,
        0xc0420f463f134000L, 0x410043c04909244eL, 0xc0430e4300433f14L,
        0x400141c341044109L, 0x2541c248c1401146L, 0x1d4202413242c046L,
        0x540062544c04401L, 0x4413471940c04631L, 0x4c034006254a0342L,
        0x13471940c140c043L, 0x3141c04003420d27L, 0x480441124300421aL,
        0x481b41134303410eL, 0x2b44044112430143L, 0x17451f4214410440L,
        0xe2c441e43174121L, 0x4517430e2c442040L, 0x3e4414400040112dL,
        0x433f204414410440L, 0xc10401d413f0c41L, 0x1245270e443f0241L,
        0x42204214431341L, 0x110b473f00471f44L, 0x1345114111420543L,
        0xc041c1423c430042L, 0x410e420b491145L, 0x114011c1400146c5L,
        0x421a402043004200L, 0x410c450843c041c3L, 0x41114424c145c942L,
        0x3c4600410d450643L, 0xc942104423c141ceL, 0x423a4900420c4306L,
        0x42cb41104522d442L, 0x3741004a0e40c040L, 0x14102400141c941L,
        0x7420642004121d5L, 0x413641014b0c41c0L, 0x4dc8410446064421L,
        0xd4422b46044a0042L, 0xc42c6400242c142L, 0xc440044401400942L,
        0x1fd4412a45c04100L, 0x45c2410042004010L, 0x40c5410340c143c4L,
        0x42024708431fd141L, 0xc0400f421843c243L, 0x42c5451040c342L,
        0x446c44e07441eccL, 0x480e431841c44300L, 0x42c4411442c24204L,
        0x410042c54e08451cL, 0xc844014210451142L, 0x42c646c3421442L,
        0xc2410544c740c04cL, 0x84100421bc64318L, 0x40c14201410d44c8L,
        0x45c5411640c34204L, 0x42ca41c24709441bL, 0xc4431941c1420042L,
        0xc41c040cc43c542L, 0x1540c3420440d344L, 0xb441ac4441741c4L,
        0x4406410341d94109L, 0x430740c3430340d5L, 0x420c4419c7411740L,
        0xc540c14400460141L, 0xd8410a440541c343L, 0x340d5420d4418c5L,
        0x421840c8400040c0L, 0x4cd7410942004205L, 0x41c340c1410142d5L,
        0x410e4417c4411941L, 0xc842c641c042d642L, 0x9460641c7410142L,
        0xd4410e4516c44119L, 0x41c940e3420a40c2L, 0x4202410140c846d4L,
        0x401141004214c540L, 0x1941c940df450b41L, 0xc1420145c941c042L,
        0xd441134313c44116L, 0x43eb440c41c24100L, 0x45cf40d540144510L,
        0xc4411642eb430143L, 0x741c2420040eb41L, 0x134510c5411541ebL,
        0x47c1420046c244deL, 0x43c643134510c541L, 0x1442eb42c743eb45L,
        0xc44216450fc64113L, 0x42dc44ff4bc34315L, 0x450fc6411342da46L,
        0xfe4cc6412ac64113L, 0x41da46fd4dc7412aL, 0xc5421340db44e541L,
        0xd64dc8412ac54014L, 0x41d946e343d54bcbL, 0x412ac54100400040L,
        0xd42d94ade45d34bL, 0xcd412ac643004002L, 0x420640da54d347ceL,
        0x4dd0402bc6430041L, 0x440543d455d448L, 0xcd4dd0412bc84400L,
        0x4101400642d352d8L, 0x43c241cc49d5402cL, 0xc741004100430041L,
        0x3400041d343c741L, 0xdd44f1402cc74000L, 0x40014702410041d3L,
        0x42e844f1410d411bL, 0xc745c24301420041L, 0xd441e742f4410e40L,
        0x1bce4402410141feL, 0x42f4412bce410041L, 0x2410141fff341c1L,
        0x402bcf4004420141L, 0xfff342c0402bcf41L, 0x3420141fff3442bL
    };

    private static final List<Pair<Integer, byte[]>> UNPACKED =
            Functional.zipWithIndex(
            Functions.map(
            new Function1Impl<byte[], List<Byte>>() {
                public byte[] call(final List<Byte> x) {
                    return ListUtils.asByteArray(x);
                }
            },
            Functional.group(asImage(), WIDTH)));
    private static final ImageConsumer[] DUMMY = new ImageConsumer[0];

    private final Set<ImageConsumer> consumers = new HashSet<ImageConsumer>(5);
    private static final ColorModel MODEL;
    private static final byte[] R = new byte[4];
    private static final byte[] G = new byte[4];
    private static final byte[] B = new byte[4];

    private static Collection<Byte> longToBytes(final Long l) {
        final List<Byte> bytes = new ArrayList<Byte>();
        for (int i = 0; i < 8; ++i) {
            final int offset = 8 * (7 - i);
            bytes.add(Byte.valueOf((byte) (0xff & (l >> offset))));
        }
        return bytes;
    }

    private static Collection<Byte> asBytes() {
        final List<Long> image1 = ListUtils.asList(image);
        final Collection<Collection<Byte>> temp = Functions.map(
                new Function1Impl<Collection<Byte>, Long>() {
                    public Collection<Byte> call(final Long l) {
                        return longToBytes(l);
                    }
                }, image1);
        return ListUtils.flatten(temp);
    }

    private static int runLength(final Byte b) {
        return (b & 0x3F) + 1;
    }

    private static Byte value(final Byte b) {
        return Byte.valueOf((byte) (0x3 & (b >> 6)));
    }

    private static Collection<Byte> decodeByte(final Byte b) {
        final List<Byte> bytes = new ArrayList<Byte>();
        final int max = runLength(b);
        final Byte value = value(b);
        for (int i = 0; i < max; ++i) {
            bytes.add(value);
        }
        return bytes;
    }

    private static List<Byte> asImage() {
        final Collection<Collection<Byte>> temp = Functions.map(
                new Function1Impl<Collection<Byte>, Byte>() {
                    public Collection<Byte> call(final Byte b) {
                        return decodeByte(b);
                    }
                }, asBytes());
        return ListUtils.flatten(temp);
    }

    static {
        for (int i = 0; i < 4; ++i) {
            R[i] = (byte) ((col[i] >> 16) & 0xFF);
            G[i] = (byte) ((col[i] >> 8) & 0xFF);
            B[i] = (byte) ((col[i]) & 0xFF);
        }
        MODEL = new IndexColorModel(3, 4, R, G, B);
    }

    public void addConsumer(final ImageConsumer ic) {
        consumers.add(ic);
    }

    public boolean isConsumer(final ImageConsumer ic) {
        return consumers.contains(ic);
    }

    public void removeConsumer(final ImageConsumer ic) {
        consumers.remove(ic);
    }

    public void startProduction(final ImageConsumer ic) {
        addConsumer(ic);
        
        Functions.each(
                new Function1Impl<Object, ImageConsumer>() {
                    public Object call(final ImageConsumer x) {
                        requestTopDownLeftRightResend(x);
                        return null;
                    }
                }, consumers.toArray(DUMMY));
    }

    public void requestTopDownLeftRightResend(final ImageConsumer ic) {
        ic.setHints(
                ImageConsumer.TOPDOWNLEFTRIGHT
                | ImageConsumer.SINGLEFRAME
                | ImageConsumer.SINGLEPASS
                | ImageConsumer.COMPLETESCANLINES);
        ic.setColorModel(MODEL);
        ic.setDimensions(WIDTH, HEIGHT);

        Functions.each(
                new Function1Impl<Object, Pair<Integer, byte[]>>() {
                    public Object call(final Pair<Integer, byte[]> row) {
                        ic.setPixels(0, row.getFirst(), InnerWorld.WIDTH, 1, 
                                MODEL, row.getSecond(),
                                0, InnerWorld.WIDTH);
                        return null;
                    }
                },
                UNPACKED);
        ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
    }
}
