/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com_ravnaandtines;
import java.util.*;
import net.sf.functionalj.tuple.Pair;
/**
 *
 * @author Alan
 */
public final class Functional {
    private Functional() {}

    public static <T> List<List<T>> group(final List<T> sequence, final int size)
    {
        int offset = 0;
        final int elements = sequence.size();
        final ArrayList<List<T>> result = new ArrayList<List<T>>((elements + size - 1)/size);
        while(offset < elements)
        {
            final int next = Math.min(offset+size, elements);
            result.add(sequence.subList(offset, next));
            offset = next;
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> List<T[]> groupToArray(final List<T> sequence, final int size)
    {
        int offset = 0;
        final int elements = sequence.size();
        final ArrayList<T[]> result = new ArrayList<T[]>((elements + size - 1)/size);
        final T[] dummy = (T[]) new Object[0];

        while(offset < elements)
        {
            final int next = Math.min(offset+size, elements);
            result.add(sequence.subList(offset, next).toArray(dummy));
            offset = next;
        }
        return result;
    }

    public static List<Integer> range(final int start, final int entries)
    {
        final int delta = entries < 0 ? -1 : 1;
        final int size = Math.abs(entries);
        int value = start;
        final List<Integer> result = new ArrayList<Integer>(size);
        for(int i=0; i<size; ++i)
        {
            result.add(value);
            value += delta;
        }
        return result;
    }

    public static <T,U> List<Pair<T,U>> zip (final Collection<T> first, final Collection<U> second)
    {
        final List<Pair<T,U>> result = new ArrayList<Pair<T,U>>();
        final Iterator<T> it1 = first.iterator();
        final Iterator<U> it2 = second.iterator();
        while(it1.hasNext() && it2.hasNext())
        {
            final Pair<T,U> pair = new Pair<T,U>(it1.next(), it2.next()); //NOPMD
            result.add(pair);
        }
        return result;
    }

    public static <T> List<Pair<Integer, T>> zipWithIndex (final Collection<T> first)
    {
        final List<Pair<Integer, T>> result = new ArrayList<Pair<Integer, T>>();
        final Iterator<T> it1 = first.iterator();
        int index = 0;
        while(it1.hasNext())
        {
            final Pair<Integer, T> pair = new Pair<Integer, T>(index, it1.next()); //NOPMD
            result.add(pair);
            ++index;
        }
        return result;
    }
}
